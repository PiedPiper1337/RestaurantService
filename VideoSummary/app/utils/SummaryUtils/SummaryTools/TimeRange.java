package utils.SummaryUtils.SummaryTools;

/**
 * Created by brianzhao on 6/4/16.
 */
public class TimeRange {
    private String startTime;
    private String endTime;

    private int startTimeSeconds;
    private int endTimeSeconds;
    private int duration;

    public TimeRange(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTimeSeconds = TimeUtils.calculateSeconds(startTime);
        this.endTimeSeconds = TimeUtils.calculateSeconds(endTime);
        this.duration = endTimeSeconds - startTimeSeconds;
    }

    public TimeRange(int startTime, int endTime) {
        this.startTime = TimeUtils.secondsToTimeString(startTime);
        this.endTime = TimeUtils.secondsToTimeString(endTime);
        this.startTimeSeconds = startTime;
        this.endTimeSeconds = endTime;
        this.duration = endTimeSeconds - startTimeSeconds;
    }

    public TimeRange(int startTime, String endTime) {
        this.startTime = TimeUtils.secondsToTimeString(startTime);
        this.endTime = endTime;
        this.startTimeSeconds = startTime;
        this.endTimeSeconds = TimeUtils.calculateSeconds(endTime);
        this.duration = endTimeSeconds - startTimeSeconds;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getEndTimeSeconds() {
        return endTimeSeconds;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getStartTimeSeconds() {
        return startTimeSeconds;
    }

    public int getDuration() {
        return duration;
    }
}
