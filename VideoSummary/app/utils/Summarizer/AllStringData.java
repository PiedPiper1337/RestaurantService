package utils.Summarizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by brianzhao on 10/31/15.
 */
public class AllStringData {
    private HashMap<String, StringData> stringDataHashMap = new HashMap<>();

    /**
     * you should only add strings that do not already exist
     * will throw a runtime exception if you attempt to add an already existing string
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
}
