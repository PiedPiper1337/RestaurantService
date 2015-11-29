package utils.Summarizer;

import models.YoutubeVideo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import play.Logger;
import utils.Constants;
import utils.StringManip;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;


/**
 * Created by brianzhao on 10/25/15.
 */
public class TranscriptFactory {
    private static final org.slf4j.Logger logger = Logger.of(TranscriptFactory.class).underlying();
    private static final int TIMEOUT = 10;
    private static volatile int numConcurrentBrowswers = 0;

    /**
     * attempts to get a string representation of the transcript from a youtube url
     *
     * @param videoId
     * @return transcript string formatted so that each 2 lines follows this pattern:
     * the start time --- end time
     * captions said during this time
     * captions do not have any newlines within them
     */
    private static YoutubeVideo createYoutubeVideoObjectFromVideoId(String videoId) {
        if (getNumConcurrentBrowswers() >= Constants.NUM_CONCURRENT_VIDEO_INFO_RETRIEVAL_ACTORS) {
            throw new RuntimeException("Too Many Active Video Retrieval Browsers. Try again later");
        }
        WebDriver browser = createWebDriver();
        WebElement moreButton = null;
        WebElement transcriptButton = null;
        WebElement transcriptContainer = null;
        WebElement videoEndTimeElement = null;
        WebElement videoTitleElement = null;

        String url = StringManip.generateUrlFromVideoId(videoId);
        String videoEndTime = null;
        String videoTitle = null;

        long startTime = System.currentTimeMillis();
        browser.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);

        try {
            browser.get(url);
            logger.debug("retrieved url: {}", url);
            videoEndTimeElement = new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.className("ytp-time-duration")));
            videoEndTime = videoEndTimeElement.getText();
            logger.debug("retrieved video end time: {}", videoEndTime);

            videoTitleElement = new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.id("eow-title")));
            videoTitle = videoTitleElement.getText();
            logger.debug("retrieved video title: {}", videoTitle);

            moreButton = new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.elementToBeClickable(By.id("action-panel-overflow-button")));
            clickElement(browser, moreButton);
            logger.debug("clicked more button");
        } catch (Exception e) {
            e.printStackTrace();
            killWebDriver(browser);
            return null;
        }

        int counter = 0;
        while (transcriptButton == null && counter < 15) {
            try {
                transcriptButton = new WebDriverWait(browser, 1).until(ExpectedConditions.elementToBeClickable(By.className("action-panel-trigger-transcript")));
            } catch (Exception e) {
                System.out.println("caught exception");
                clickElement(browser, moreButton);
                System.out.println("clicked more button");
                counter++;
            }
        }
        if (counter == 15) {
            killWebDriver(browser);
            return null;
        }

        //click and wait for transcript to load
        try {
            clickElement(browser, transcriptButton);
            transcriptContainer = new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.id("transcript-scrollbox")));
            logger.debug("transcript successfully loaded into webdriver");

            Document doc = Jsoup.parse(transcriptContainer.getAttribute("innerHTML"));
            StringBuilder rawTranscript = new StringBuilder();
            LinkedHashMap<String, String> timeToText = new LinkedHashMap<>();

            for (Element timeRegion : doc.body().children()) {
                Elements timeRegionData = timeRegion.children();

                //first element is always time, second is always text
                String currentTime = timeRegionData.get(0).text();
                String currentCaption = timeRegionData.get(1).text();
                //check for possible repeated times, and concatenate captions if that is the case
                if (timeToText.containsKey(currentTime)) {
                    timeToText.put(currentTime, timeToText.get(currentTime) + currentCaption.trim());
                } else {
                    timeToText.put(currentTime, currentCaption.trim());
                }
            }

            Iterator<String> keyIterator = timeToText.keySet().iterator();
            if (!keyIterator.hasNext()) { //need to have transcript of at least 2 regions
                return null;
            }

            String time = keyIterator.next(); //get time
            while (keyIterator.hasNext()) {
                if (!timeToText.get(time).isEmpty()) { //if the current string isn't empty
                    String segmentStartTime = time;
                    time = keyIterator.next();
                    String segmentEndTime = time;
                    String contents = timeToText.get(segmentStartTime).replaceAll("\n", " ");
                    rawTranscript.append(segmentStartTime).append(Constants.TIME_REGION_DELIMITER).append(segmentEndTime).append('\n').append(contents).append('\n');
                } else {
                    time = keyIterator.next();
                }
            }

            //do last time
            if (!timeToText.get(time).isEmpty()) { //if the current string isn't empty
                String contents = timeToText.get(time).replaceAll("\n", " ");
                rawTranscript.append(time).append(Constants.TIME_REGION_DELIMITER).append(videoEndTime).append('\n').append(contents);
            }

            logger.debug("transcript successfully parsed");
            long finishTime = System.currentTimeMillis();
            logger.debug("time taken: {}", (finishTime - startTime) * 1.0 / 1000);
            killWebDriver(browser);
            return new YoutubeVideo(videoId, rawTranscript.toString(), videoTitle);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            killWebDriver(browser);
            return null;
        }
    }

    /**
     * will get the transcript, and perform appropriate lookups and saves in the database
     *
     * @param inputString the videoID or the full url of the video
     * @return the transcript in the form of a string
     */
    public static Transcript getTranscript(String inputString) {
        String videoId = StringManip.isFullUrl(inputString) ? StringManip.getVideoId(inputString) : inputString;
        String rawTranscriptString;
        YoutubeVideo youtubeVideo = YoutubeVideo.find.where().eq("videoId", videoId).findUnique();
        if (youtubeVideo == null) {
            logger.debug("video hasn't been seen before");
            youtubeVideo = TranscriptFactory.createYoutubeVideoObjectFromVideoId(videoId);
            //TODO make all exceptions throw up to application level
            rawTranscriptString = youtubeVideo.getTranscript();
            if (rawTranscriptString == null) {
                logger.error("critical error trying to get transcript for video: {}", videoId);
                return null;
            }
            youtubeVideo.save();
            logger.debug("video transcript saved in database");
        } else {
            logger.debug("using database transcript");
            rawTranscriptString = youtubeVideo.getTranscript();
        }
        return new Transcript(rawTranscriptString);
    }

    /**
     * https://stackoverflow.com/questions/12035023/selenium-webdriver-cant-click-on-a-link-outside-the-page
     *
     * @param element
     */
    private static void clickElement(WebDriver browser, WebElement element) {
        /**
         * -150 is necessary for firefox.
         * this is absolutely ridiculous
         */
        int elementPosition = element.getLocation().getY() - 100;
        String js = String.format("window.scroll(0, %s)", elementPosition);
        logger.debug(js);
        ((JavascriptExecutor) browser).executeScript(js);
        element.click();
    }

    private static synchronized WebDriver createWebDriver() {
        numConcurrentBrowswers++;
        return new ChromeDriver();
    }

    private static synchronized void killWebDriver(WebDriver webDriver) {
        webDriver.quit();
        numConcurrentBrowswers--;
    }

    private static synchronized int getNumConcurrentBrowswers() {
        return numConcurrentBrowswers;
    }

}
