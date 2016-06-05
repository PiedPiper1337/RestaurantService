package modules;

import com.google.inject.AbstractModule;
import play.Logger;
import utils.GlobalState;

/**
 * Created by brianzhao on 10/25/15.
 * https://www.playframework.com/documentation/2.4.x/JavaDependencyInjection#Stopping/cleaning-up
 */

public class InitializerModule extends AbstractModule {
    private static final org.slf4j.Logger logger = Logger.of(InitializerModule.class).underlying();

    @Override
    protected void configure() {
        //figures out what current OS is and stores it in global state
        logger.debug("Determining operating system...");
        determineOS();
        determineChromeDriver();
    }

    private void determineOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        logger.debug("Detected operating system: {}", osName);
        if (osName.startsWith("mac")) {
            GlobalState.operatingSystem = GlobalState.OS.Mac;
        } else if (osName.startsWith("window")) {
            GlobalState.operatingSystem = GlobalState.OS.Windows;
        } else {
            GlobalState.operatingSystem = GlobalState.OS.Linux;
        }
    }

    private void determineChromeDriver() {
        logger.debug("Setting chrome driver environment variable...");
        if (GlobalState.operatingSystem == GlobalState.OS.Mac) {
            System.setProperty("webdriver.chrome.driver", "chromedriverMac");
        } else if (GlobalState.operatingSystem == GlobalState.OS.Linux) {
            System.setProperty("webdriver.chrome.driver", "chromedriverLinux");
        } else {
            throw new RuntimeException("Windows not supported yet");
        }
        logger.debug("chromedriver environment path set");
    }
}


