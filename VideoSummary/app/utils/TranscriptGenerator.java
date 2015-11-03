package utils;

import com.google.inject.Inject;
import models.YoutubeVideo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import play.Logger;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;


/**
 * Created by brianzhao on 10/25/15.
 */
public class TranscriptGenerator {
    private static final org.slf4j.Logger logger = Logger.of(TranscriptGenerator.class).underlying();
    private static final int TIMEOUT = 10;

    @Inject
    private static WebDriver browser;

    private static String downloadTranscriptFromVideoID(String videoId) {
        return downloadTranscriptFromFullURL(StringManip.generateUrlFromVideoId(videoId));
    }

    /**
     * attempts to get a string representation of the transcript from a youtube url
     *
     * @param url
     * @return transcript string formatted so that each 2 lines follows this pattern:
     * the start time --- end time
     * captions said during this time
     * captions do not have any newlines within them
     */
    private static String downloadTranscriptFromFullURL(String url) {
        long startTime = System.currentTimeMillis();
        browser.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);
        WebElement moreButton = null;
        WebElement transcriptButton = null;
        WebElement transcriptContainer = null;
        WebElement videoEndTimeElement = null;
        String videoEndTime = null;

        try {
            browser.get(url);
            logger.debug("retrieved url: {}", url);
            videoEndTimeElement = new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.className("ytp-time-duration")));
            videoEndTime = videoEndTimeElement.getText();
            logger.debug("retrieved video end time: {}", videoEndTime);
            moreButton = new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.elementToBeClickable(By.id("action-panel-overflow-button")));
            moreButton.click();
            logger.debug("clicked more button");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        int counter = 0;
        while (transcriptButton == null && counter < 15) {
            try {
                transcriptButton = new WebDriverWait(browser, 1).until(ExpectedConditions.elementToBeClickable(By.className("action-panel-trigger-transcript")));
            } catch (Exception e) {
                System.out.println("caught exception");
                moreButton.click();
                System.out.println("clicked more button");
                counter++;
            }
        }
        if (counter == 15) {
            return null;
        }

        //click and wait for transcript to load
        try {
            Thread.sleep(1000);
            transcriptButton.click();
            transcriptContainer = new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.id("transcript-scrollbox")));
            logger.debug("transcript successfully loaded");

            Document doc = Jsoup.parse(transcriptContainer.getAttribute("innerHTML"));
            StringBuilder toReturn = new StringBuilder();
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
                    toReturn.append(segmentStartTime).append(Constants.TIME_REGION_DELIMITER).append(segmentEndTime).append('\n').append(contents).append('\n');
                } else {
                    time = keyIterator.next();
                }
            }

            //do last time
            if (!timeToText.get(time).isEmpty()) { //if the current string isn't empty
                String contents = timeToText.get(time).replaceAll("\n", " ");
                toReturn.append(time).append(Constants.TIME_REGION_DELIMITER).append(videoEndTime).append('\n').append(contents);
            }


            logger.debug("transcript successfully parsed");
            long finishTime = System.currentTimeMillis();
            logger.debug("time taken: {}", (finishTime - startTime) * 1.0 / 1000);
            //before returning, we should reset browser

            //if you're on a dev machine redirect to localhost 9000, otherwise 80
            if (GlobalState.operatingSystem == GlobalState.OS.Mac) {
                browser.get("http://localhost:9000/blank");
            } else {
                browser.get("http://localhost/blank");
            }
            return toReturn.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * will get the transcript, and perform appropriate lookups and saves in the database
     *
     * @param inputString the videoID or the full url of the video
     * @return the transcript in the form of a string
     */
    public static String getTranscript(String inputString) {
        String videoId = StringManip.isFullUrl(inputString) ? StringManip.getVideoId(inputString) : inputString;
        String transcript;
        YoutubeVideo youtubeVideo = YoutubeVideo.find.where().eq("videoId", videoId).findUnique();
        if (youtubeVideo == null) {
            logger.debug("video hasn't been seen before");
            transcript = TranscriptGenerator.downloadTranscriptFromVideoID(videoId);
            if (transcript == null) {
                logger.error("critical error trying to get transcript for video: {}", videoId);
                return null;
            }
            youtubeVideo = new YoutubeVideo(videoId, transcript);
            youtubeVideo.save();
            logger.debug("video transcript saved in database");
        } else if (youtubeVideo.getTranscript() == null) {
            logger.debug("filling in nonexistent transcript");
            transcript = TranscriptGenerator.downloadTranscriptFromVideoID(videoId);
            if (transcript == null) {
                logger.error("critical error trying to get transcript for video: {}", videoId);
                return null;
            }
            youtubeVideo.setTranscript(transcript);
            youtubeVideo.save();
            logger.debug("video transcript saved in database");
        } else {
            logger.debug("using database transcript");
            transcript = youtubeVideo.getTranscript();
        }
        return transcript;
    }
}
