package utils.SummaryUtils.SummaryTools;

import org.apache.commons.lang.ArrayUtils;

/**
 * Created by brianzhao on 6/4/16.
 */
public class TimeUtils {
    public static int calculateSeconds(String timeString) {
        String[] timeArray = timeString.split(":");
        if (timeArray.length > 3) {
            throw new RuntimeException("Time span to convert to seconds exceeds time unit of hours, Time span longer than hours detected");
        }

        ArrayUtils.reverse(timeArray);
        int currentUnit = 1; //1 second is worth 1 second; i'll multiply this by 60 when we get to minutes, etc
        int secondsCount = 0;
        for (int i = 0; i < timeArray.length; i++) {
            secondsCount += Integer.parseInt(timeArray[i]) * currentUnit;
            currentUnit *= 60;
        }
        return secondsCount;
    }

    public static String secondsToTimeString(int seconds) {
        int numSecondsRemaining = seconds;
        /**
         * youtube apparently will represent the string for a time over an hour
         * still using minutes: eg
         * https://www.youtube.com/watch?v=z8HKWUWS-lA
         * 1 hour 17 minutes is: 79:17, so i'm not going to have an hour section in my code
         */
//        int hours = seconds/3600;
//        numSecondsRemaining = numSecondsRemaining % 3600;

        int minutes = numSecondsRemaining / 60;
        numSecondsRemaining = numSecondsRemaining % 60;
        StringBuilder resultantTimeString = new StringBuilder();
        resultantTimeString.append(minutes).append(':');
        /**
         * catch the posibility of something like 0:02
         * and return that instead of 0:2
         */
        if (numSecondsRemaining < 10) {
            resultantTimeString.append('0');
        }
        resultantTimeString.append(numSecondsRemaining);
        return resultantTimeString.toString();
    }
}
