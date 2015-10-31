package utils.Summarizer;

import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Victor Kwak
 * 5/3/15
 */
public class Group implements Comparable<Group> {
    public double totalImportance = 0;
    private ArrayList<Range> group = new ArrayList<>();

    public void add(Range range) {
        totalImportance += range.importance;
        group.add(range);
    }

    public Range get(int i) {
        return group.get(i);
    }

    public int size() {
        return group.size();
    }

    @Override
    public int compareTo(Group o) {
//        return Double.compare(o.totalImportance , this.totalImportance );
        String[] startTime = this.get(0).startTime.split(":");
        String[] startTime2 = o.get(0).startTime.split(":");
        int startTimeInt = Integer.valueOf(startTime[0]) * 60 + Integer.valueOf(startTime[1]);
        int endTimeInt = Integer.valueOf(startTime2[0]) * 60 + Integer.valueOf(startTime2[1]);
        return (startTimeInt - endTimeInt);
        //return Double.compare(Integer.parseInt())
    }

    public int groupLength() {
        String[] startTime = group.get(0).startTime.split(":");
        String[] endTime = group.get(group.size() - 1).startTime.split(":");
        return Integer.parseInt(endTime[0]) - Integer.parseInt(startTime[0]);
    }

    public String toString() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add(group.get(0).startTime + " - " + group.get(group.size() - 1).startTime);
        group.forEach(range -> sj.add(range.contents));
        return sj.toString();
    }
}
