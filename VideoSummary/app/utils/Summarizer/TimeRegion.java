package utils.Summarizer;
import org.apache.commons.lang.ArrayUtils;
import play.Logger;

import java.util.HashMap;

public class TimeRegion  {
    private static final org.slf4j.Logger logger = Logger.of(TimeRegion.class).underlying();
    private String startTime;
    private String endTime;

    private int startTimeSeconds;
    private int endTimeSeconds;

    //the words said during this TimeRegion
    private String captionString;

    //the localTF values
    private HashMap<String, Double> localTF = new HashMap<>();

    private double[] tfIdfVector;
    private double importance = 0;
    private double groupImportance = 0;

    public TimeRegion(String startTime, String endTime, String captionString) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTimeSeconds = calculateSeconds(startTime);
        this.endTimeSeconds = calculateSeconds(endTime);
        this.captionString = captionString;
    }

    public String getCaptionString() {
        return captionString;
    }

    public void setCaptionString(String captionString) {
        this.captionString = captionString;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getEndTimeSeconds() {
        return endTimeSeconds;
    }

    public void setEndTimeSeconds(int endTimeSeconds) {
        this.endTimeSeconds = endTimeSeconds;
    }

    public double getGroupImportance() {
        return groupImportance;
    }

    public void setGroupImportance(double groupImportance) {
        this.groupImportance = groupImportance;
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

    @Override
    public String toString() {
        return "TimeRegion{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", captionString='" + captionString + '\'' +
                '}';
    }
}