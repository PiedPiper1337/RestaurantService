package utils.Factories;

import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by brianzhao on 10/25/15.
 */
public class ChromeFactory {
    private static final ChromeDriver chromeDriver = new ChromeDriver();
    public static ChromeDriver getInstance() {
        return chromeDriver;
    }

}
