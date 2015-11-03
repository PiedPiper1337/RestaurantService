package utils.Summarizer;

import java.util.Comparator;

/**
 * Created by brianzhao on 10/31/15.
 */
public class TimeRegionComparators {
    public static Comparator<TimeRegion> startTimeComparator = new Comparator<TimeRegion>() {
        @Override
        public int compare(TimeRegion o1, TimeRegion o2) {
            return Integer.compare(o1.getStartTimeSeconds(),o2.getStartTimeSeconds());
        }
    };

    public static Comparator<TimeRegion> importanceComparator = new Comparator<TimeRegion>() {
        @Override
        public int compare(TimeRegion o1, TimeRegion o2) {
            return Double.compare(o1.getImportance(),o2.getImportance());
        }
    };
}
