
package utils;


import global.PlayGlobal;
import org.openqa.selenium.WebDriver;

import javax.inject.Inject;


/**
 * Created by brianzhao on 10/25/15.
 */
public class TranscriptGenerator {
    @Inject
    private static WebDriver browser;

    public static String getTranscriptFromVideoID(String videoId) {
        System.out.println(browser);

        return null;
    }

    public static String getTranscriptFromFullURL(String url) {
        return getTranscriptFromVideoID(StringManip.getVideoId(url));
    }
}
