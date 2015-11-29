package utils.Summarizer;

import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.libs.Json;
import utils.Constants;

import java.util.*;

/**
 * Created by brianzhao && victorkwak
 */
public class SimpleFrequencySummary implements Summary {
    private static final org.slf4j.Logger logger = Logger.of(SimpleFrequencySummary.class).underlying();
    //histogram represents list of all timestamped regions of the video
    private Transcript transcript;

    private Double topWords;
    private Weight weightType;
    private Double cutOffValue;
    private Double percentageOfVideo;
    private Boolean normalizeOnDuration;

    //list of all words - used to identify the word for each index of the TFIDF vector
    private List<String> wordOrdering = new ArrayList<>();


    public SimpleFrequencySummary(Transcript input) {
        //creates the histogram of ranges
        this.transcript = input;

        //calculates all tf, df, tfidf, and local tf frequencies
        transcript.analyzeWordCount();
    }

    public List<Group> generateSummary() {
        return this.generateSummary(null, null, null, null, null);
    }

    public List<Group> generateSummary(Double percentageOfTopwords,
                                       Double percentageOfVideo,
                                       Double cutOffValue,
                                       Weight weightType,
                                       Boolean normalizeOnDuration) {
        resetMembers();

        //topwords reflects the proportion of the highest value words which will be deemed "important"
        this.topWords = percentageOfTopwords;
//        this.topWords = (percentageOfTopwords == null ? Constants.DEFAULT_PERCENTAGE_TOPWORDS : percentageOfTopwords );
        this.weightType = (weightType == null ? Constants.DEFAULT_WEIGHT_TYPE : weightType);
        this.cutOffValue = cutOffValue; // this one waits until after determining importances
        this.percentageOfVideo = percentageOfVideo == null ? Constants.DEFAULT_SUMMARY_PROPORTION : percentageOfVideo;
        this.normalizeOnDuration = normalizeOnDuration == null ? Constants.DEFAULT_NORMALIZE_ON_DURATION : normalizeOnDuration;

        //determines which way to sort all the words in the document
        //tf will sort by sheer numbers
        //tf-idf will attempt to find the best representative words of the document
        if (percentageOfTopwords != null) {
            assignImportanceValuesDiscretely(this.transcript, this.weightType, this.topWords, this.normalizeOnDuration);
        } else {
            assignImportanceValues(this.transcript, this.weightType, this.normalizeOnDuration);
        }

        if (cutOffValue == null) {
            this.cutOffValue = determineCutoffImportance(this.transcript);
        }

        return createSummaryGroups(this.transcript, this.cutOffValue, this.normalizeOnDuration);
    }

    @Override
    public Map<String, Integer> generateWordCloud() {
        Map<String, Integer> toReturn = new LinkedHashMap<>();
        AllStringData allStringData = transcript.getAllStringData();
        List<StringData> stringDataList = allStringData.getWordCloudData(Weight.TF, Constants.WORD_CLOUD_SIZE);
        stringDataList.forEach(stringData -> toReturn.put(allStringData.getUnstemmedVersion(stringData.getWord()), (int) stringData.getTf()));
        return toReturn;
    }

    private List<Group> createSummaryGroups(Transcript transcript, double cutOffValue, boolean normalizeOnDuration) {
        List<Group> groups = createGroups(transcript, cutOffValue, normalizeOnDuration);
        Collections.sort(groups, Collections.reverseOrder(normalizeOnDuration ? GroupComparators.normalizedTotalImportance : GroupComparators.totalImportance));


        double calculatedLengthOfSummaryInSeconds = determineDurationOfSummary();
        double secondsCurrentlyInSummary = 0;

        List<Group> finalSummary = new ArrayList<>();
        int counter = 0;
        while (secondsCurrentlyInSummary <= calculatedLengthOfSummaryInSeconds) {
            finalSummary.add(groups.get(counter));
            secondsCurrentlyInSummary += groups.get(counter).getTotalDuration();
            counter++;
        }

        Collections.sort(finalSummary, GroupComparators.startTime);
        return finalSummary;
    }


    private void resetMembers() {
        topWords = null;
        weightType = null;
        cutOffValue = null;
        percentageOfVideo = null;
        normalizeOnDuration = null;
    }


    private double determineDurationOfSummary() {
        double lengthOfVideo = transcript.getDurationOfVideo();
        return Math.min(Constants.DEFAULT_SUMMARY_DURATION_SECONDS, lengthOfVideo * this.percentageOfVideo);
    }

    /**
     * returns a JsonNode of a list of each timeregion within a transcript, followed by its importance value
     * only works if all of the importance values have been set
     *
     * @param
     * @return
     */
    public JsonNode histogram() {
        if (this.transcript.isImportanceValuesSet()) {
            List<TimeRegion> timeRegions = this.transcript.getTimeRegions();
            Collections.sort(timeRegions, TimeRegionComparators.startTimeComparator);
            List<JsonNode> jsonList = new ArrayList<>();
            timeRegions.forEach(timeRegion -> jsonList.add(timeRegion.histogramComponent()));
            return Json.toJson(jsonList);
        } else {
            throw new RuntimeException("Importance Values not Set");
        }
    }


    /**
     * sorts the transcript's timeRegions by importance, and gets the median importance value
     * requires importance values be set
     *
     * @param transcript
     * @return
     */
    private double determineCutoffImportance(Transcript transcript) {
        if (transcript.isImportanceValuesSet()) {
            List<TimeRegion> timeRegions = transcript.getTimeRegions();
            Collections.sort(timeRegions, TimeRegionComparators.importanceComparator);
            return timeRegions.get((int) (timeRegions.size() * Constants.DEFAULT_CUTOFF)).getImportance();
        } else {
            throw new RuntimeException("Importance Values of Transcript TimeRegions not set");
        }
    }

    /**
     * will assign the importance values to each of the time regions of the transcript,
     * this happens by sorting all strings by their associated frequency (either tf or tfidf frequency), which is the second argument
     * the third argument is the number of words of the list of strings after being sorted that are considered "important"
     * only timeregions that contain "important" words will have importance values greater than 0
     *
     * @param transcript
     * @param weightType
     * @param proportionOfWordsDeemedImportant must be between 0 (exclusive) and 1 (inclusive)
     * @param normalizeOnDuration              choose whether to have importance values be divided by the length of the timeregion
     */
    private void assignImportanceValuesDiscretely(Transcript transcript, Weight weightType, double proportionOfWordsDeemedImportant, boolean normalizeOnDuration) {
        if (proportionOfWordsDeemedImportant > 1 || proportionOfWordsDeemedImportant <= 0) {
            throw new RuntimeException("Bad Proportion Value passed in");
        }
        AllStringData allStringData = transcript.getAllStringData();
        List<StringData> sortedStringData = null;
        if (weightType == Weight.TF) {
            sortedStringData = allStringData.sortByTfDescending();
        } else if (weightType == Weight.TFIDF) {
            sortedStringData = allStringData.sortByTfIdfDescending();
        } else {
            //no other weighting techniques done yet
            throw new RuntimeException("Bad weight was given");
        }
        HashSet<StringData> mostImportantWords = new HashSet<>(sortedStringData.subList(0, (int) (proportionOfWordsDeemedImportant * sortedStringData.size())));

        List<TimeRegion> timeRegions = transcript.getTimeRegions();
        for (TimeRegion currentTimeRegion : timeRegions) {
            Map<String, Double> currentTF = currentTimeRegion.getLocalTF();
            double calculatedImportance = 0;
            for (String containedWord : currentTF.keySet()) {
                //this is hacky and bad since i have to create a new stringdata object each time, this works because
                //i overrode hashcode and equals
                if (mostImportantWords.contains(new StringData(containedWord))) {
                    calculatedImportance += currentTF.get(containedWord) * allStringData.getWeight(containedWord, weightType);
                }
            }
            if (normalizeOnDuration) {
                calculatedImportance /= currentTimeRegion.getDuration();
            }
            currentTimeRegion.setImportance(calculatedImportance);
        }
        transcript.setImportanceValuesSet(true);
    }

    /**
     * will assign the importance values to each of the time regions of the transcript,
     * this happens by sorting all strings by their associated frequency (either tf or tfidf frequency), which is the second argument
     * the third argument is the number of words of the list of strings after being sorted that are considered "important"
     * each timeregion's importance is equal to the summed "importance" of the words included in the region, divided by the
     * number of seconds it takes
     *
     * @param transcript
     * @param weightType
     * @param normalizeOnDuration
     */
    private void assignImportanceValues(Transcript transcript, Weight weightType, boolean normalizeOnDuration) {
        List<TimeRegion> timeRegions = transcript.getTimeRegions();
        AllStringData allStringData = transcript.getAllStringData();
        for (TimeRegion currentTimeRegion : timeRegions) {
            Map<String, Double> currentTF = currentTimeRegion.getLocalTF();
            double calculatedImportance = 0;
            for (String containedWord : currentTF.keySet()) {
                calculatedImportance += currentTF.get(containedWord) * allStringData.getWeight(containedWord, weightType);
            }
            if (normalizeOnDuration) {
                calculatedImportance /= currentTimeRegion.getDuration();
            }
            currentTimeRegion.setImportance(calculatedImportance);
        }
        transcript.setImportanceValuesSet(true);
    }

    private List<Group> createGroups(Transcript transcript, double cutOffValue, boolean normalizeOnDuration) {
        boolean inWord = false;
        List<Group> groups = new ArrayList<>();
        Group group = null;
        List<TimeRegion> timeRegions = transcript.getTimeRegions();
        Collections.sort(timeRegions, TimeRegionComparators.startTimeComparator);
        int totalNumberOfTimeRegions = timeRegions.size();
        for (int i = 0; i < totalNumberOfTimeRegions; i++) {
            TimeRegion timeRegion = timeRegions.get(i);
            //if the importance is greater than the cutOffValue,
            if (timeRegion.getImportance() > cutOffValue) {
                if (!inWord) {
                    inWord = true;
                    group = new Group();
                    group.add(timeRegion, normalizeOnDuration);
                } else {
                    group.add(timeRegion, normalizeOnDuration);
                }
                if (i == totalNumberOfTimeRegions - 1) {
                    inWord = false;
                    groups.add(group);
                }
            } else {
                if (inWord) {
                    inWord = false;
                    groups.add(group);
                }
            }
        }
        return groups;
    }


    /**
     * this would only be used for clustering purposes
     */
//    private void createDocumentTFIDFVectors() {
//
//        int vectorLength = wordOrdering.size();
//        for (TimeRegion timeRegion : histogram) {
//            timeRegion.tfIdfVector = new double[vectorLength];
//            for (int i = 0; i < vectorLength; i++) {
//                String currentString = wordOrdering.get(i);
//                if (!timeRegion.localTF.containsKey(currentString)) {
//                    timeRegion.tfIdfVector[i] = 0;
//                } else {
//                    timeRegion.tfIdfVector[i] = (1 + Math.log10(timeRegion.localTF.get(currentString))) * (Math.log10(histogram.size() / df.get(currentString)));
//                }
//            }
//        }
//        normalizeDocumentTFIDFVectors();
//    }
//
//    private void normalizeDocumentTFIDFVectors() {
//        //NORMALIZATION!!!!!!
//        for (TimeRegion timeRegion : histogram) {
//            double sum = 0;
//            for (double a : timeRegion.tfIdfVector) {
//                sum += a * a;
//            }
//            sum = Math.sqrt(sum + 1);
//            for (int i = 0; i < timeRegion.tfIdfVector.length; i++) {
//                timeRegion.tfIdfVector[i] = timeRegion.tfIdfVector[i] / sum;
//            }
//        }
//    }
//    private void SummationSummary() {
//
//
//        System.out.println("Sorted TFIDF values of all words in transcript: ");
//        //to see sorted Tf-IDF Values
//        for (int i = 0; i < sortedTFIDF.size(); i++) {
//            System.out.println(stemmedToUnstemmed.get(sortedTFIDF.get(i).word) + "\t\t" + sortedTFIDF.get(i).count);
//        }
//
//        System.out.println("\n\n\n");
//
//        System.out.println("Sorted TF values of all words in transcript: ");
//        //to see sorted TF-values
//        for (int i = 0; i < sortedTF.size(); i++) {
//            System.out.println(stemmedToUnstemmed.get(sortedTF.get(i).word) + "\t\t" + sortedTF.get(i).count);
//        }
//
//    }


}
