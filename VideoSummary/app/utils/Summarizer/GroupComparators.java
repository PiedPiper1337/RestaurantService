package utils.Summarizer;

import java.util.Comparator;

/**
 * Created by brianzhao on 10/31/15.
 */
public class GroupComparators {
    public static Comparator<Group> normalizedTotalImportance =
            (o1, o2) -> Double.compare(o1.getTotalImportance() / o1.getTotalDuration(),
                    o2.getTotalImportance() / o2.getTotalDuration());

    public static Comparator<Group> totalImportance =
            (o1, o2) -> Double.compare(o1.getTotalImportance(), o2.getTotalImportance());

    public static Comparator<Group> startTime =
            (o1, o2) -> Double.compare(o1.getStartTimeSeconds(), o2.getStartTimeSeconds());
}
