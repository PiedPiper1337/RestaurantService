package utils.SummaryUtils.SummaryTools;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Brian Zhao && Victor Kwak
 * 5/3/15
 */
public class Group {
    private ArrayList<TimeRegion> group = new ArrayList<>();
    private double totalImportance = 0;
    private double totalDuration = 0;

    private String startTime;
    private String endTime;

    private int startTimeSeconds;
    private int endTimeSeconds;


    /**
     * only semantically makes sense if the timeregion to add is immediately adjacent to the last added timeregion
     *
     * @param timeRegion
     * @param timeRegionImportanceIsDividedByDurationAlready
     */
    public void add(TimeRegion timeRegion, boolean timeRegionImportanceIsDividedByDurationAlready) {
        if (!group.isEmpty() && !group.get(group.size() - 1).isAdjacentTo(timeRegion)) {
            throw new RuntimeException("Attempted to add nonconsecutive timeregion");
        }

        if (timeRegionImportanceIsDividedByDurationAlready) {
            totalImportance += timeRegion.getImportance() * timeRegion.getDuration();
        } else {
            totalImportance += timeRegion.getImportance();
        }
        totalDuration += timeRegion.getDuration();
        endTime = timeRegion.getEndTime();
        endTimeSeconds = TimeUtils.calculateSeconds(endTime);
        group.add(timeRegion);
        if (size() == 1) {
            startTime = group.get(0).getStartTime();
            startTimeSeconds = TimeUtils.calculateSeconds(startTime);
        }
    }

    public TimeRegion get(int i) {
        return group.get(i);
    }

    public TimeRegion getLast() {
        if (group.isEmpty()) {
            return null;
        } else {
            return group.get(group.size() - 1);
        }
    }

    public int size() {
        return group.size();
    }


    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        for (TimeRegion timeRegion : group) {
            toReturn.append(timeRegion.toString()).append('\n');
        }
        return toReturn.toString();
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public double getTotalImportance() {
        return totalImportance;
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

    public JsonNode getJson() {
        Map jsonMap = new HashMap();
        jsonMap.put("totalImportance", totalImportance);
        jsonMap.put("totalDuration", totalDuration);
        jsonMap.put("startTime", startTime);
        jsonMap.put("endTime", endTime);
        jsonMap.put("startTimeSeconds", startTimeSeconds);
        jsonMap.put("endTimeSeconds", endTimeSeconds);
        StringBuilder wordsSpokenInGroup = new StringBuilder();
        for (TimeRegion timeRegion : group) {
            wordsSpokenInGroup.append(timeRegion.getCaptionString()).append('\n');
        }
        jsonMap.put("wordsSpoken", wordsSpokenInGroup.toString());
        return Json.toJson(jsonMap);
    }
}