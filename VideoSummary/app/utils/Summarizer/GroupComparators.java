package utils.Summarizer;

import java.util.Comparator;

/**
 * Created by brianzhao on 10/31/15.
 */
public class GroupComparators {
    public static Comparator<Group> normalizedTotalImportance = new Comparator<Group>() {
        @Override
        public int compare(Group o1, Group o2) {
            return Double.compare(o1.getTotalImportance() / o1.getTotalDuration(),
                    o2.getTotalImportance() / o2.getTotalDuration());
        }
    };

    public static Comparator<Group> totalImportance = new Comparator<Group>() {
        @Override
        public int compare(Group o1, Group o2) {
            return Double.compare(o1.getTotalImportance(), o2.getTotalImportance());
        }
    };

    public static Comparator<Group> startTime = new Comparator<Group>() {
        @Override
        public int compare(Group o1, Group o2) {
            return Double.compare(o1.getStartTimeSeconds(), o2.getStartTimeSeconds());
        }
    };
}
