package utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import play.Logger;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

/**
 * Created by brianzhao on 10/13/15.
 */
public class StringManip {
    private static final org.slf4j.Logger logger = Logger.of(StringManip.class).underlying();
    public static String extractParameter(String url, String key) {
        String[] entireUrlString = url.split("\\?");
        String params = entireUrlString[1];
        String[] keyValue = params.split("&");
        HashMap<String, String> keyValueMap = new HashMap<>();

        for (int i = 0; i < keyValue.length; i++) {
            String[] k = keyValue[i].split("=");
            keyValueMap.put(k[0], k[1]);
        }

        if (!keyValueMap.containsKey(key)) {
            throw new RuntimeException(); // We couldn't extract what we were looking for
        }

        String toReturn = keyValueMap.get(key);

        if (toReturn == null) {
            throw new RuntimeException();
        }

        return toReturn;
    }

    /**
     * @param url entire youtube video url, eg: https://www.youtube.com/watch?v=Rh_NXwqFvHc
     * @return the vidoe id portion, the part after the v=
     */
    public static String getVideoId(String url) {
        return extractParameter(url, "v");
    }

    //should probably be stronger check in the future
    public static boolean isFullUrl(String input) {
        return input.contains("youtube.com");
    }

    public static String generateUrlFromVideoId(String videoId) {
        return Constants.BASE_YOUTUBE_URL + Constants.WATCH_URL_APPEND + videoId;
    }
}
