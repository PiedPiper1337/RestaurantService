import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import play.Application;
import play.GlobalSettings;
import play.Logger;

import javax.inject.Inject;

/**
 * Created by brianzhao on 10/11/15.
 */
public class Global extends GlobalSettings {
    enum OSType{
        Linux,Mac,Windows
    }

    private static final org.slf4j.Logger logger = Logger.of(Global.class).underlying();

    @Override
    public void onStart(Application application) {
        logger.trace("Application has started");
        OSType currentOS;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("mac")) {
            currentOS = OSType.Mac;
        }else if (osName.startsWith("window")) {
            currentOS = OSType.Windows;
        } else {
            currentOS = OSType.Linux;
        }
        logger.debug("Detected operating system: {}", currentOS);


        if (currentOS.equals(OSType.Mac)) {
            System.setProperty("webdriver.chrome.driver", "chromedriverMac");
        }else if (currentOS.equals(OSType.Linux)) {
            System.setProperty("webdriver.chrome.driver", "chromedriverLinux");
        }
        logger.debug("chromedriver environment path set");
    }

    @Override
    public void onStop(Application application) {
        logger.trace("Application shutdown...");
    }
}

