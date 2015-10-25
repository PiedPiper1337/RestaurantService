package modules;

import org.openqa.selenium.chrome.ChromeDriver;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by brianzhao on 10/25/15.
 */

@Singleton
public class WebDriverImpl extends ChromeDriver{
    enum OSType{
        Linux,Mac,Windows
    }

    public WebDriverImpl() {
        OSType currentOS;
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
        super();

    }
}