package global;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import play.Application;
import play.GlobalSettings;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by brianzhao on 10/11/15.
 */
public class PlayGlobal extends GlobalSettings {
    private static final org.slf4j.Logger logger = Logger.of(PlayGlobal.class).underlying();
    public static OSType currentOS;

    @Inject
    public static WebDriver browser;

    @Override
    public void onStart(Application application) {
        logger.trace("Application has started");

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("mac")) {
            currentOS = OSType.Mac;
        }else if (osName.startsWith("window")) {
            currentOS = OSType.Windows;
        } else {
            currentOS = OSType.Linux;
        }

        if (currentOS.equals(OSType.Mac)) {
            System.setProperty("webdriver.chrome.driver", "chromedriverMac");
        }else if (currentOS.equals(OSType.Linux)) {
            System.setProperty("webdriver.chrome.driver", "chromedriverLinux");
        }
        System.out.println(currentOS);

        browser = new ChromeDriver();
        logger.debug("ChromeDriver has started");

    }

    @Override
    public void onStop(Application application) {
        logger.trace("Application shutdown...");
    }
}

enum OSType{
    Linux,Mac,Windows
}