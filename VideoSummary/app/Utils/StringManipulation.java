package Utils;

import java.util.HashMap;

/**
 * Created by brianzhao on 10/13/15.
 */
public class StringManipulation {
    public static String extractParameter(String url, String key) {
        String[] entireUrlString= url.split("\\?");
        if (entireUrlString.length != 2) {
            throw new RuntimeException();
        }
        String params = entireUrlString[1];
        String[] keyValue = params.split("=");
        if (keyValue.length % 2 != 0) {
            throw new RuntimeException();
        }
        HashMap<String, String> keyValueMap = new HashMap<>();

        for (int i = 0; i < keyValue.length; i+=2) {
            keyValueMap.put(keyValue[i], keyValue[i + 1]);
        }
        String toReturn = keyValueMap.get(key);
        if (toReturn == null) {
            throw new RuntimeException();
        }
        return toReturn;
    }
}
