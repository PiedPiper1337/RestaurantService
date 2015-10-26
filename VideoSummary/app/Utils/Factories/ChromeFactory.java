package Utils.Factories;

import org.openqa.selenium.chrome.ChromeDriver;
import play.Logger;

/**
 * Created by brianzhao on 10/25/15.
 */
public class ChromeFactory {
    private static final org.slf4j.Logger logger = Logger.of(ChromeFactory.class).underlying();
    private static ChromeDriver chromeDriver = null;

    public static ChromeDriver getInstance() {
        if (chromeDriver == null) {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.startsWith("mac")) {
                System.setProperty("webdriver.chrome.driver", "chromedriverMac");
            } else if (osName.startsWith("window")) {
                //don't handle this for now
            } else {
                System.setProperty("webdriver.chrome.driver", "chromedriverLinux");
            }
            logger.debug("Detected operating system: {}", osName);
            chromeDriver = new ChromeDriver();
            return chromeDriver;
        } else {
            return chromeDriver;
        }

    }

}
