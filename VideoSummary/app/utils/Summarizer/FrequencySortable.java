package utils.Summarizer;

import java.util.ArrayList;

/**
 * Created by brianzhao on 10/31/15.
 */

public interface FrequencySortable {
    public ArrayList<StringData> sortByTf();
    public ArrayList<StringData> sortByTfIdf();
    public ArrayList<StringData> sortByTfDescending();
    public ArrayList<StringData> sortByTfIdfDescending();
}
