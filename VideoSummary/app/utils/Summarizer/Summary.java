package utils.Summarizer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by brianzhao && victorkwak
 */
public class Summary {
    //histogram represents list of all timestamped regions of the video
    private Transcript transcript;

    //list of stopwords that are not counted
    //TODO make this injected??
    private static StopWords stop = new StopWords();

    //takes input as stemmed string, returs the unstemmed string
    private HashMap<String, String> stemmedToUnstemmed = new HashMap<>();

    //proportion of highest tf-idf words that are considered as "important" to increment score
    private double topWords;
    private Weight weightType;
    private double cutOffValue;

    //list of all words, sorted by their global tf-idf weight in increasing order
    private ArrayList<StringData> sortedTFIDF = new ArrayList<>();
    private ArrayList<StringData> sortedTF = new ArrayList<>();

    //list of all words - used to identify the word for each index of the TFIDF vector
    private ArrayList<String> wordOrdering = new ArrayList<>();


    public Summary(String input,
                   double percentageOfTopwords,
                   double percentageOfVideo,
                   double cutOffValue,
                   Weight weightType) throws Exception {

        //creates the histogram of ranges
        transcript = new Transcript(input);

        //calculates all tf, df, tfidf, and local tf frequencies
        transcript.analyzeWordCount();

        FrequencySortable sortable = transcript.getSortableStringData();




        //topwords reflects the proportion of the highest tf-idf value words which will be deemed "important"
        this.topWords = percentageOfTopwords;
        this.weightType = weightType;
        this.cutOffValue = cutOffValue;

        SummationSummary();

    }





    private void createSorted() {
        for (String z : tfIdf.keySet()) {
            sortedTFIDF.add(new StringData(z, tfIdf.get(z)));
            sortedTF.add(new StringData(z, tf.get(z)));
            wordOrdering.add(z);
        }
        Collections.sort(sortedTFIDF);
        Collections.sort(sortedTF);
        createDocumentTFIDFVectors();
    }

    private void createDocumentTFIDFVectors() {
        int vectorLength = wordOrdering.size();
        for (TimeRegion timeRegion : histogram) {
            timeRegion.tfIdfVector = new double[vectorLength];
            for (int i = 0; i < vectorLength; i++) {
                String currentString = wordOrdering.get(i);
                if (!timeRegion.localTF.containsKey(currentString)) {
                    timeRegion.tfIdfVector[i] = 0;
                } else {
                    timeRegion.tfIdfVector[i] = (1 + Math.log10(timeRegion.localTF.get(currentString))) * (Math.log10(histogram.size() / df.get(currentString)));
                }
            }
        }
        normalizeDocumentTFIDFVectors();
    }

    private void normalizeDocumentTFIDFVectors() {
        //NORMALIZATION!!!!!!
        for (TimeRegion timeRegion : histogram) {
            double sum = 0;
            for (double a : timeRegion.tfIdfVector) {
                sum += a * a;
            }
            sum = Math.sqrt(sum + 1);
            for (int i = 0; i < timeRegion.tfIdfVector.length; i++) {
                timeRegion.tfIdfVector[i] = timeRegion.tfIdfVector[i] / sum;
            }
        }
    }

    private void SummationSummary() {

        ArrayList<StringData> orderToBeConsidered = new ArrayList<>();
        if (weightType == Weight.TF) {
            orderToBeConsidered = sortedTF;
        } else if (weightType == Weight.TFIDF) {
            orderToBeConsidered = sortedTFIDF;
        }

        //iterate through the timeregions of the histogram
        for (TimeRegion timeRegion : histogram) {
            //for the topMost words in the arraylist sorted, i.e. the top percentage of the tf-idf weighted words
            for (int j = (int) ((1 - topWords) * orderToBeConsidered.size()); j < orderToBeConsidered.size(); j++) {
                String currentWord = orderToBeConsidered.get(j).word;
                //if the current timeRegion localTF field contains this word, increment the timeRegion's importance by the global tf-idf weight of the word
                if (timeRegion.localTF.containsKey(currentWord)) {
                    double termImportance = 0;
                    if (weightType == Weight.TF) {
                        termImportance = tf.get(currentWord);
                    } else if (weightType == Weight.TFIDF) {
                        termImportance = tfIdf.get(currentWord);
                    }
                    timeRegion.importance += termImportance;
                }
            }
        }

        //this is what our histogram looks like
//        for (TimeRegion range : histogram) {
//            System.out.println(range.importance);
//            System.out.println(range.captionString);
//        }

        ArrayList<TimeRegion> newHistogram = new ArrayList<>(histogram);
        Collections.sort(newHistogram, new Comparator<TimeRegion>() {
            @Override
            public int compare(TimeRegion o1, TimeRegion o2) {
                return Double.compare(o1.importance, o2.importance);
            }
        });
//        System.out.println("\n\n\nRESTARTING");
//        for (TimeRegion range : newHistogram) {
//            System.out.println(range.importance);
//            System.out.println(range.captionString);
//        }
        this.cutOffValue = newHistogram.get(newHistogram.size() / 2).importance;

        ArrayList<Group> condensedRegions = createGroups();
        Collections.sort(condensedRegions);
        System.out.println("Number of Groups: " + condensedRegions.size());

        for (Group group1 : condensedRegions) {
            System.out.println(group1.get(0).startTime + " - " + group1.get(group1.size() - 1).startTime);
            group1.print();
            System.out.println();
        }

        System.out.println("Sorted TFIDF values of all words in transcript: ");
        //to see sorted Tf-IDF Values
        for (int i = 0; i < sortedTFIDF.size(); i++) {
            System.out.println(stemmedToUnstemmed.get(sortedTFIDF.get(i).word) + "\t\t" + sortedTFIDF.get(i).count);
        }

        System.out.println("\n\n\n");

        System.out.println("Sorted TFIDF values of all words in transcript: ");
        //to see sorted TF-values
        for (int i = 0; i < sortedTF.size(); i++) {
            System.out.println(stemmedToUnstemmed.get(sortedTF.get(i).word) + "\t\t" + sortedTF.get(i).count);
        }

    }

    private ArrayList<Group> createGroups() {
        boolean inWord = false;
        ArrayList<Group> groups = new ArrayList<>();
        Group group = null;
        int histogramSize = histogram.size();
        for (int i = 0; i < histogramSize; i++) {
            TimeRegion timeRegion = histogram.get(i);
            //if the importance is greater than the cutOffValue,
            if (timeRegion.importance > cutOffValue) {
                if (!inWord) {
                    inWord = true;
                    group = new Group();
                    group.add(timeRegion);
                } else {
                    group.add(timeRegion);
                }
                if (i == histogramSize - 1) {
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


}
