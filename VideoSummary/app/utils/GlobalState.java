package utils;


import java.net.URL;

/**
 * Created by brianzhao on 10/30/15.
 */
public class GlobalState {
    public enum OS {
        Mac, Linux, Windows
    }

    public static OS operatingSystem;

    public static URL seleniumURL;
}
