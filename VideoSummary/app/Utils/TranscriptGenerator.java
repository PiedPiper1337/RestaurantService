package Utils;

import com.google.inject.Inject;
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

import java.util.concurrent.TimeUnit;


/**
 * Created by brianzhao on 10/25/15.
 */
public class TranscriptGenerator {
    private static final org.slf4j.Logger logger = Logger.of(TranscriptGenerator.class).underlying();

    private static final int TIMEOUT = 3;

    @Inject
    private static WebDriver browser;

    public static String getTranscriptFromVideoID(String videoId) {
        return getTranscriptFromFullURL(StringManip.generateUrlFromVideoId(videoId));
    }

    /**
     * attempts to get a string representation of the transcript from a youtube url
     *
     * @param url
     * @return
     */
    public static String getTranscriptFromFullURL(String url) {
        long startTime = System.currentTimeMillis();
        browser.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);
        WebElement moreButton = null;
        WebElement transcriptButton = null;
        WebElement transcriptContainer = null;

        try {
            browser.get(url);
            moreButton = new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.elementToBeClickable(By.id("action-panel-overflow-button")));
            moreButton.click();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        logger.debug("Loaded url: {}, and clicked more button", url);

        int counter = 0;
        while (transcriptButton == null && counter < 5) {
            try {
                transcriptButton = new WebDriverWait(browser, 1).until(ExpectedConditions.elementToBeClickable(By.className("action-panel-trigger-transcript")));
            } catch (Exception e) {
                System.out.println("caught exception");
                moreButton.click();
                System.out.println("clicked more button");
                counter++;
            }
        }
        if (counter == 5) {
            return null;
        }

        //click and wait for transcript to load
        try {
            transcriptButton.click();
            transcriptContainer = new WebDriverWait(browser, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.id("transcript-scrollbox")));
            logger.debug("transcript successfully loaded");

            Document doc = Jsoup.parse(transcriptContainer.getAttribute("innerHTML"));
            StringBuilder toReturn = new StringBuilder();
            for (Element element : doc.body().children()) {
                Elements caption = element.children();
                //first element is always time, second is always text
                for (Element element1 : caption) {
                    toReturn.append(element1.text() + "\n");
                }
            }
            logger.debug("transcript successfully parsed");
            long finishTime = System.currentTimeMillis();
            logger.debug("time taken: {}", (finishTime - startTime) * 1.0 / 1000);
            //before returning, we should reset browser
            browser.get("http://www.blankwebsite.com/");
            return toReturn.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
