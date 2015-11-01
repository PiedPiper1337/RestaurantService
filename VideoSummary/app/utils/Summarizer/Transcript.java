package utils.Summarizer;

import utils.Constants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by brianzhao on 10/31/15.
 */
public class Transcript {
    private ArrayList<TimeRegion> timeRegions;
    private String entireTranscript;
    private AllStringData allStringData = new AllStringData();
    private static StopWords stopWords = new StopWords();
    private double durationOfVideo;
    private boolean analyzedYet = false;
    private boolean importanceValuesSet = false;

    public Transcript(String entireTranscript) {
        this.entireTranscript = entireTranscript;
        timeRegions = buildTimeRegionsFromString(entireTranscript);
    }

    private ArrayList<TimeRegion> buildTimeRegionsFromString(String entireTranscript) {
        ArrayList<TimeRegion> toReturn = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(entireTranscript.getBytes())));
            String currentLine;
            String startTime = null;
            String endTime = null;

            int counter = 0;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (counter % 2 == 0) {
                    /*
                        if its even, then its a time value; works because transcriptgenerator guarantees
                        that there are no newlines within the captions themselves
                    */
                    String time = currentLine;
                    String[] timeArray = currentLine.trim().split(Constants.TIME_REGION_DELIMITER);
                    if (timeArray.length != 2) {
                        throw new RuntimeException("Expected a start time and end time after splitting on time region delimiter. Array size not 2");
                    }
                    startTime = timeArray[0];
                    endTime = timeArray[1];
                } else {
                    if (startTime == null || endTime == null) {
                        throw new RuntimeException("StartTime or EndTime was null. Should never happen");
                    }
                    toReturn.add(new TimeRegion(startTime, endTime, currentLine));
                    startTime = null;
                    endTime = null;
                }
                counter++;
            }
            if (counter % 2 != 0) {
                throw new RuntimeException("Number of lines was not even. Means bufferedreader didn't get everything! Or there's an extra newline");
            }
            this.durationOfVideo = toReturn.get(toReturn.size() - 1).getEndTimeSeconds();
            return toReturn;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException while trying to make transcript object from string");
        }
    }

    /**
     * call this method to generate all data regarding tf, df and tfidf counts for this transcript
     * will update allStringData, which has data about the entire transcript's tf, df and idf counts
     * will also set each timeregion's localTF count
     */
    public void analyzeWordCount() {
        analyzedYet = true;
        for (TimeRegion timeRegion : timeRegions) {
            //convert all words to lowercase and remove any punctuation
            String[] words = timeRegion.getCaptionString().toLowerCase().replaceAll("[^\\w ]", "").split("\\s+");

            //holder corresponds to the field "localTF" for each timeRegion object
            HashMap<String, Double> localTf = new HashMap<>();
            for (String currentWord : words) {

                //skip the word if it is a stopword
                if (stopWords.isStopWord(currentWord)) {
                    continue;
                }
                //stem the current word
                String currentWordStemmed = stemWord(currentWord);

                //if the stemmed word isn't inside the hashmap, add it w/a frequency of 1
                if (!localTf.containsKey(currentWordStemmed)) {
                    localTf.put(currentWordStemmed, 1.0);

                    //increment the word's associated document frequency as well, since this is the first time
                    //the word has occurred in this timeregion
                    if (!allStringData.containsString(currentWordStemmed)) {
                        allStringData.addString(currentWordStemmed);
                        allStringData.updateDF(currentWordStemmed, 1.0);
                        allStringData.setUnstemmedVersion(currentWordStemmed, currentWord);
                    } else {
                        allStringData.updateDF(currentWordStemmed, allStringData.getDF(currentWordStemmed) + 1);
                    }
                }
                //otherwise , increment the value in the holder hashmap
                else {
                    localTf.put(currentWordStemmed, localTf.get(currentWordStemmed) + 1);
                }
            }
            localTf.remove("");
            for (String containedWord : localTf.keySet()) {
                //increment the global tf frequency associated with this word
                if (!allStringData.containsString(containedWord)) {
                    throw new RuntimeException("All string data should have picked up this word when updating Document Frequency");
                } else {
                    allStringData.updateTF(containedWord, allStringData.getTF(containedWord) + localTf.get(containedWord));
                }
            }
            timeRegion.setLocalTF(localTf);
        }
        allStringData.removeEmptyString();
        //calculate all tf-idf frequencies
        for (String containedString : allStringData.allContainedStrings()) {
            allStringData.updateTfIdf(containedString, computeTf_Idf(containedString));
        }
    }

    public boolean isAnalyzedYet() {
        return analyzedYet;
    }

    public ArrayList<TimeRegion> getTimeRegions() {
        return timeRegions;
    }

    private double computeTf_Idf(String t) {
        double logNormalizedTf = 1 + Math.log10(allStringData.getTF(t)); //log normalized tf
        double inverseDf = Math.log10(timeRegions.size() / allStringData.getDF(t)); //inverse freq
        return logNormalizedTf * inverseDf;
    }

    public double getDurationOfVideo() {
        return durationOfVideo;
    }

    /**
     * be sure to not modify any of the string data contained within here.
     *
     * @return
     */
    public AllStringData getAllStringData() {
        return allStringData;
    }

    public String stemWord(String inputString) {
        Stemmer s = new Stemmer();
        char[] word = inputString.toCharArray();
        s.add(word, word.length);
        s.stem();
        return s.toString();
    }

    public boolean isImportanceValuesSet() {
        return importanceValuesSet;
    }

    public void setImportanceValuesSet(boolean importanceValuesSet) {
        this.importanceValuesSet = importanceValuesSet;
    }


    @Override
    public String toString() {
        return entireTranscript;
    }
}
