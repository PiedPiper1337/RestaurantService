package utils.Summarizer;
import org.apache.commons.lang.ArrayUtils;
import play.Logger;

import java.util.HashMap;

public class TimeRegion {
    private static final org.slf4j.Logger logger = Logger.of(TimeRegion.class).underlying();
    private String startTime;
    private String endTime;

    private int startTimeSeconds;
    private int endTimeSeconds;
    private int duration;

    //the words said during this TimeRegion
    private String captionString;

    //the localTF values
    private HashMap<String, Double> localTF = new HashMap<>();

    private double[] tfIdfVector;
    private double importance = 0;

    public TimeRegion(String startTime, String endTime, String captionString) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTimeSeconds = calculateSeconds(startTime);
        this.endTimeSeconds = calculateSeconds(endTime);
        this.duration = endTimeSeconds - startTimeSeconds;
        this.captionString = captionString;
    }

    public TimeRegion(int startTime, int endTime, String captionString) {
        this.startTime = secondsToTimeString(startTime);
        this.endTime = secondsToTimeString(endTime);
        this.startTimeSeconds = startTime;
        this.endTimeSeconds = endTime;
        this.duration = endTimeSeconds - startTimeSeconds;
        this.captionString = captionString;
    }

    public TimeRegion(int startTime, String endTime, String captionString) {
        this.startTime = secondsToTimeString(startTime);
        this.endTime = endTime;
        this.startTimeSeconds = startTime;
        this.endTimeSeconds = calculateSeconds(endTime);
        this.duration = endTimeSeconds - startTimeSeconds;
        this.captionString = captionString;
    }





    public String getCaptionString() {
        return captionString;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getEndTimeSeconds() {
        return endTimeSeconds;
    }

    public double getImportance() {
        return importance;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }


    public HashMap<String, Double> getLocalTF() {
        return localTF;
    }

    public void setLocalTF(HashMap<String, Double> localTF) {
        this.localTF = localTF;
    }

    public String getStartTime() {
        return startTime;
    }


    public double[] getTfIdfVector() {
        return tfIdfVector;
    }

    public void setTfIdfVector(double[] tfIdfVector) {
        this.tfIdfVector = tfIdfVector;
    }

    public int getStartTimeSeconds() {
        return startTimeSeconds;
    }

    public static int calculateSeconds(String timeString) {
        String[] timeArray = timeString.split(":");
        if (timeArray.length > 3) {
            logger.error("Time span to convert to seconds exceeds time unit of hours");
            throw new RuntimeException("Time span longer than hours detected");
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

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "TimeRegion{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", captionString='" + captionString + '\'' +
                '}' + '\n';
    }
}