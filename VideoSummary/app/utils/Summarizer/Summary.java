package utils.Summarizer;

import java.util.List;
import java.util.Map;

/**
 * Created by brianzhao on 11/19/15.
 */
public interface Summary {
    List<Group> generateSummary();

    Map<String, Integer> generateWordCloud();

}
