package modules;

import com.google.inject.AbstractModule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import play.Logger;
import utils.ChromeDriverCustom;
import utils.FirefoxDriverCustom;
import utils.GlobalState;
import utils.Summarizer.TranscriptFactory;

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
        requestStaticInjection(TranscriptFactory.class);

        /**
         * use chromedriver for mac
         */
        if (GlobalState.operatingSystem == GlobalState.OS.Mac || GlobalState.operatingSystem == GlobalState.OS.Linux) {
            //figures out what type of chromedriver to use based on os and sets the environment variable for it
            logger.debug("Setting chrome driver environment variable...");
            bind(WebDriver.class).to(ChromeDriverCustom.class).asEagerSingleton();
        }

//        else if (GlobalState.operatingSystem == GlobalState.OS.Linux){
//            logger.debug("using firefoxdriver...");
//            bind(WebDriver.class).to(FirefoxDriverCustom.class).asEagerSingleton();
//        }
        else {
            throw new RuntimeException("WINDOWS NOT SUPPORTED YET");
        }
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


