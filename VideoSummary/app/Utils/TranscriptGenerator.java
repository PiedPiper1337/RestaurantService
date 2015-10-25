package utils;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;


/**
 * Created by brianzhao on 10/25/15.
 */
public class TranscriptGenerator {

    private static final int TIMEOUT = 3;

    public static String getTranscriptFromVideoID(String videoId, WebDriver browser) {
        return getTranscriptFromFullURL(StringManip.generateUrlFromVideoId(videoId), browser);
    }

    public static String getTranscriptFromFullURL(String url, WebDriver browser) {
        browser.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);


        return null;
    }
}
