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
        try {
            List<NameValuePair> results = URLEncodedUtils.parse(new URL(url).toURI(), HTTP.UTF_8);
            for (NameValuePair nameValuePair : results) {
                if (nameValuePair.getName().equalsIgnoreCase(key)) {
                    logger.debug(nameValuePair.getValue());
                    return nameValuePair.getValue();
                }
            }
            logger.error("key: {} in url: {} not found", key, url);
            return null;
        } catch (Exception e) {
            logger.error("exception with url {}" , url);
            e.printStackTrace();
            return null;
        }
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
