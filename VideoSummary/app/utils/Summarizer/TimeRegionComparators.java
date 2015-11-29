package utils.Summarizer;

import java.util.Comparator;

/**
 * Created by brianzhao on 10/31/15.
 */
public class TimeRegionComparators {
    public static Comparator<TimeRegion> startTimeComparator = (o1, o2) -> Integer.compare(o1.getStartTimeSeconds(),o2.getStartTimeSeconds());

    public static Comparator<TimeRegion> importanceComparator = (o1, o2) -> Double.compare(o1.getImportance(),o2.getImportance());
}
