package utils.Summarizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by brianzhao on 10/31/15.
 */
public class AllStringData {
    private HashMap<String, StringData> stringDataHashMap = new HashMap<>();

    /**
     * you should only add strings that do not already exist
     * will throw a runtime exception if you attempt to add an already existing string
     *
     * @param input
     * @return
     */
    public void addString(String input) {
        if (containsString(input)) {
            throw new RuntimeException("Attempted to add already existing string");
        }
        stringDataHashMap.put(input, new StringData(input));
    }

    public boolean containsString(String input) {
        return stringDataHashMap.containsKey(input);
    }

    public double getTF(String inputString) {
        if (!containsString(inputString)) {
            throw new RuntimeException("Attempted to getTF of non existing string");
        }
        return stringDataHashMap.get(inputString).getTf();
    }

    public double getDF(String inputString) {
        if (!containsString(inputString)) {
            throw new RuntimeException("Attempted to getDf of non existing string");
        }
        return stringDataHashMap.get(inputString).getDf();
    }

    public double getTfIdF(String inputString) {
        if (!containsString(inputString)) {
            throw new RuntimeException("Attempted to getTfIdf of non existing string");
        }
        return stringDataHashMap.get(inputString).getTfIdf();
    }

    public String getUnstemmedVersion(String inputString) {
        if (!containsString(inputString)) {
            throw new RuntimeException("Attempted to lookup non existing string");
        }
        return stringDataHashMap.get(inputString).getUnstemmedWord();
    }

    public void setUnstemmedVersion(String inputString, String unstemmedInputString) {
        if (!containsString(inputString)) {
            throw new RuntimeException("Attempted to lookup non existing string");
        }
        stringDataHashMap.get(inputString).setUnstemmedWord(unstemmedInputString);
    }


    public void updateTF(String inputString, double tf) {
        if (!containsString(inputString)) {
            throw new RuntimeException("Attempted to updateTF of non existing string");
        }
        stringDataHashMap.get(inputString).setTf(tf);
    }

    public void updateDF(String inputString, double df) {
        if (!containsString(inputString)) {
            throw new RuntimeException("Attempted to updateDf of non existing string");
        }
        stringDataHashMap.get(inputString).setDf(df);
    }

    public void updateTfIdf(String inputString, double df) {
        if (!containsString(inputString)) {
            throw new RuntimeException("Attempted to updateTfIdf of non existing string");
        }
        stringDataHashMap.get(inputString).setTfIdf(df);
    }

    public ArrayList<StringData> sortByTf() {
        ArrayList<StringData> stringDatas = new ArrayList<>(stringDataHashMap.values());
        Collections.sort(stringDatas, StringDataComparators.tfCompare);
        return stringDatas;
    }

    public ArrayList<StringData> sortByTfIdf() {
        ArrayList<StringData> stringDatas = new ArrayList<>(stringDataHashMap.values());
        Collections.sort(stringDatas, StringDataComparators.tfIdfCompare);
        return stringDatas;
    }

    public ArrayList<StringData> sortByTfDescending() {
        ArrayList<StringData> stringDatas = new ArrayList<>(stringDataHashMap.values());
        Collections.sort(stringDatas, Collections.reverseOrder(StringDataComparators.tfCompare));
        return stringDatas;
    }

    public ArrayList<StringData> sortByTfIdfDescending() {
        ArrayList<StringData> stringDatas = new ArrayList<>(stringDataHashMap.values());
        Collections.sort(stringDatas, Collections.reverseOrder(StringDataComparators.tfIdfCompare));
        return stringDatas;
    }

    public void removeEmptyString() {
        if (stringDataHashMap.containsKey("")) {
            stringDataHashMap.remove("");
        }
    }

    public Set<String> allContainedStrings() {
        return stringDataHashMap.keySet();
    }

    public double getWeight(String inputString, Weight weightType) {
        if (!containsString(inputString)) {
            throw new RuntimeException("Attempted to lookup non existing string");
        }
        if (weightType == Weight.TF) {
            return stringDataHashMap.get(inputString).getTf();
        } else if (weightType == Weight.TFIDF) {
            return stringDataHashMap.get(inputString).getTfIdf();
        } else {
            throw new RuntimeException("unsupported weighttype");
        }
    }

}
