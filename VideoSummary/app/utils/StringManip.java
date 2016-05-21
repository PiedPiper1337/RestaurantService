package utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import play.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by brianzhao on 10/13/15.
 */
public class StringManip {
    private static final org.slf4j.Logger logger = Logger.of(StringManip.class).underlying();

    public static String extractParameter(String url, String key) {
        if (url.matches("https?://youtu\\.be/.*")) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        try {
            List<NameValuePair> list = URLEncodedUtils.parse(new URI(url), HTTP.UTF_8);
            for (NameValuePair nameValuePair : list) {
                if (nameValuePair.getName().equalsIgnoreCase(key)) {
                    return nameValuePair.getValue();
                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI failed to parse");
        }
        throw new RuntimeException("Failed to obtain parameter");
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
        return input.matches("(https?://www\\.youtube\\.com/watch\\?v=.*)|(https?://youtu\\.be/.*)");
    }

    public static String generateUrlFromVideoId(String videoId) {
        return Constants.BASE_YOUTUBE_URL + Constants.WATCH_URL_APPEND + videoId;
    }
}
