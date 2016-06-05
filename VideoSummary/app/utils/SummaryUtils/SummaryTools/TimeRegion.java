package utils.SummaryUtils.SummaryTools;

import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;

public class TimeRegion {
    private static final org.slf4j.Logger logger = Logger.of(TimeRegion.class).underlying();
    private TimeRange timeRange;

    //the words said during this TimeRegion
    private String captionString;

    //the localTF values
    private Map<String, Double> localTF = new HashMap<>();

    private double[] tfIdfVector;
    private double importance = 0;

    public TimeRegion(String startTime, String endTime, String captionString) {
        this.timeRange = new TimeRange(startTime, endTime);
        this.captionString = captionString;
    }

    public TimeRegion(int startTime, int endTime, String captionString) {
        this.timeRange = new TimeRange(startTime, endTime);
        this.captionString = captionString;
    }

    public TimeRegion(int startTime, String endTime, String captionString) {
        this.timeRange = new TimeRange(startTime, endTime);
        this.captionString = captionString;
    }

    public TimeRegion(TimeRange timeRange, String captionString) {
        this.timeRange = timeRange;
        this.captionString = captionString;
    }

    public String getCaptionString() {
        return captionString;
    }

    public String getEndTime() {
        return timeRange.getEndTime();
    }

    public int getEndTimeSeconds() {
        return timeRange.getEndTimeSeconds();
    }

    public double getImportance() {
        return importance;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }


    public Map<String, Double> getLocalTF() {
        return localTF;
    }

    public void setLocalTF(Map<String, Double> localTF) {
        this.localTF = localTF;
    }

    public String getStartTime() {
        return timeRange.getStartTime();
    }

    public int getStartTimeSeconds() {
        return timeRange.getStartTimeSeconds();
    }

    public double[] getTfIdfVector() {
        return tfIdfVector;
    }

    public void setTfIdfVector(double[] tfIdfVector) {
        this.tfIdfVector = tfIdfVector;
    }

    public int getDuration() {
        return timeRange.getDuration();
    }

    public JsonNode histogramComponent() {
        Map toJsonify = new HashMap();
        toJsonify.put("startTimeSeconds", timeRange.getStartTimeSeconds());
        toJsonify.put("importance", importance);
        return Json.toJson(toJsonify);
    }

    @Override
    public String toString() {
        return "TimeRegion{" +
                "startTime='" + timeRange.getStartTime() + '\'' +
                ", endTime='" + timeRange.getEndTime() + '\'' +
                ", captionString='" + captionString + '\'' +
                '}' + '\n';
    }

    /**
     * determines whether the "other" timeregion immediately follows or precedes
     * this timeregion, assuming "other" comes from the same transcript object
     * @param other another timeregion object coming from the same Transcript
     * @return
     */
    public boolean isAdjacentTo(TimeRegion other){
        return this.getEndTime().equals(other.getStartTime())
                || this.getStartTime().equals(other.getEndTime());
    }
}