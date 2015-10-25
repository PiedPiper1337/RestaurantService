package utils.Factories;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by brianzhao on 10/25/15.
 */
public class PhantomFactory {
    private static PhantomJSDriver phantomJsDriver;
    public static PhantomJSDriver getInstance() {
        if (phantomJsDriver == null) {
            Capabilities caps = new DesiredCapabilities();
            ((DesiredCapabilities) caps).setJavascriptEnabled(true);
            ((DesiredCapabilities) caps).setCapability(
                    PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                    "phantomjs"
            );
            phantomJsDriver = new PhantomJSDriver(caps);
            return phantomJsDriver;
        } else {
            return phantomJsDriver;
        }

    }

}
