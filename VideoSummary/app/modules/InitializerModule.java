package modules;

import com.google.inject.AbstractModule;
import play.Logger;
import utils.GlobalState;

import java.net.MalformedURLException;
import java.net.URL;

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
        determineSeleniumUrl();
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

    private void determineSeleniumUrl() {
        logger.debug("Setting chrome driver environment variable...");
        //todo fix this hardcoding with config/environment read
        if (GlobalState.operatingSystem == GlobalState.OS.Mac) {
            try {
                GlobalState.seleniumURL = new URL("http://localhost:4444/wd/hub");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        } else if (GlobalState.operatingSystem == GlobalState.OS.Linux) {
            try {
                GlobalState.seleniumURL = new URL("http://192.168.2.10:4444/wd/hub");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException("Windows not supported yet");
        }
        logger.debug("chromedriver environment path set");
    }
}


