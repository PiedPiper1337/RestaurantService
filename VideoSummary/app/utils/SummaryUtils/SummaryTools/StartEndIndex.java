package utils.SummaryUtils.SummaryTools;

/**
 * Created by brianzhao on 6/4/16.
 */

/**
 * intended to wrap the starting and ending index of a substring's occurrence
 * inside a larger string.
 * Currently used to help map strings from an outside summarizer into timeregion
 * objects
 * <p>
 * goal: map timregion indexes -> timeregion,
 * substring lookup to get indexes
 * sort the keyset of the map
 * then append together regions that "contain" the indexes
 */
public class StartEndIndex {
    private final int start;
    private final int end;

    public StartEndIndex(int end, int start) {
        this.end = end;
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public boolean intersects(StartEndIndex other) {
        //absolute inclusion
        if (this.getStart() <= other.getStart() && this.getEnd() >= other.getEnd()) {
            return true;
        } else if (this.getStart() <= other.getStart() && other.getStart() < this.getEnd()) {
            //"beginning of other" inclusion
            return true;
        } else if (this.getStart() < other.getEnd() && this.getEnd() >= other.getEnd()) {
            //"end of other" inclusion
            return true;
        }
        return false;
    }
}
