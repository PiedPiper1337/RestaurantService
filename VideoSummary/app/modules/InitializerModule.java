package modules;

import com.google.inject.AbstractModule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import play.Logger;

/**
 * Created by brianzhao on 10/25/15.
 */
public class InitializerModule extends AbstractModule {
    private static final org.slf4j.Logger logger = Logger.of(InitializerModule.class).underlying();

    enum OSType {
        Linux, Mac, Windows
    }

    @Override
    protected void configure() {
        OSType currentOS;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("mac")) {
            currentOS = OSType.Mac;
        } else if (osName.startsWith("window")) {
            currentOS = OSType.Windows;
        } else {
            currentOS = OSType.Linux;
        }
        logger.debug("Detected operating system: {}", currentOS);

        if (currentOS.equals(OSType.Mac)) {
            System.setProperty("webdriver.chrome.driver", "chromedriverMac");
        } else if (currentOS.equals(OSType.Linux)) {
            System.setProperty("webdriver.chrome.driver", "chromedriverLinux");
        }
        logger.debug("chromedriver environment path set");
        bind(WebDriver.class).to(ChromeDriver.class).asEagerSingleton();
    }
}


