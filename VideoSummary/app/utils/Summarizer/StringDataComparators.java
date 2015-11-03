package utils.Summarizer;

import java.util.Comparator;

/**
 * Created by brianzhao on 10/31/15.
 */
public class StringDataComparators {
    public static Comparator<StringData> tfCompare = new Comparator<StringData>() {
        @Override
        public int compare(StringData o1, StringData o2) {
            return Double.compare(o1.getTf(),o2.getTf());
        }
    };

    public static Comparator<StringData> dfCompare = new Comparator<StringData>() {
        @Override
        public int compare(StringData o1, StringData o2) {
            return Double.compare(o1.getDf(), o2.getDf());
        }
    };

    public static Comparator<StringData> tfIdfCompare = new Comparator<StringData>() {
        @Override
        public int compare(StringData o1, StringData o2) {
            return Double.compare(o1.getTfIdf(), o2.getTfIdf());
        }
    };
}
