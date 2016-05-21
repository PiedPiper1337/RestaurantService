package utils.Summarizer;

import java.util.Map;

/**
 * Created by brianzhao on 11/21/15.
 * this interface is not being used in the code just yet, i'm leaving it here in case i decide I want to make timeregion more versatile
 */
public interface TimeSection {
    String getStartTime();

    int getStartTimeSeconds();

    String getEndTime();

    int getEndTimeSeconds();

    int getDuration();

    String getCaptionString();

    double getImportance();

    void setImportance(double importance);

    Map<String, Double> getLocalTF();

    void setLocalTF(Map<String, Double> localTF);

    double[] getTfIdfVector();

    void setTfIdfVector(double[] tfIdfVector);
}
