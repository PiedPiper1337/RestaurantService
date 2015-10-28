package utils.Summarizer;
import java.util.Comparator;
import java.util.HashMap;

public class Range implements Comparable<Range> {
    public String startTime;
    public String endTime;
    public String contents;
    public HashMap<String, Double> counts = new HashMap<>();
    public double importance = 0;
    public double groupImportance = 0;
    int indexLocation;

    public Range(String startTime,String endTime , String contents, int indexLocation) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.contents = contents;
        this.indexLocation = indexLocation;
    }

    public Range(String startTime, String contents, int indexLocation) {
        this.startTime = startTime;
        this.contents = contents;
        this.indexLocation = indexLocation;
    }
    public String toString() {
        return startTime + " --> " + endTime + "\n"
                + contents + "\n";
    }

    public int compareTo(Range o) {
        return Double.compare(this.groupImportance,  o.groupImportance);
    }
}
