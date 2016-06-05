package utils.SummaryUtils.SummaryTools;

import java.util.Comparator;

/**
 * Created by brianzhao on 10/31/15.
 */
public class StringDataComparators {
    public static Comparator<StringData> tfCompare = (o1, o2) -> Double.compare(o1.getTf(),o2.getTf());

    public static Comparator<StringData> dfCompare = (o1, o2) -> Double.compare(o1.getDf(), o2.getDf());

    public static Comparator<StringData> tfIdfCompare = (o1, o2) -> Double.compare(o1.getTfIdf(), o2.getTfIdf());
}
