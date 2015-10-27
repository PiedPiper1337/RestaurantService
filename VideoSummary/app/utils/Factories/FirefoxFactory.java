package utils.Factories;

import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by brianzhao on 10/25/15.
 */
public class FirefoxFactory {
    private static final FirefoxDriver firefoxDriver= new FirefoxDriver();
    public static FirefoxDriver getInstance() {
        return firefoxDriver;
    }

}
