package utils.Factories;

import org.openqa.selenium.WebDriver;

/**
 * Created by brianzhao on 10/25/15.
 */
public class WebDriverFactory {
    public static WebDriver getInstance() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            return ChromeFactory.getInstance();
        } else {
            return FirefoxFactory.getInstance();
        }
    }
}
