package utils.Summarizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Summary {
    private static ArrayList<Range> histogram = new ArrayList<>();
    private HashMap<String, Double> tf = new HashMap<>();
    private HashMap<String, Double> df = new HashMap<>();
    private HashMap<String, Double> tfidf = new HashMap<>();
    private StopWords stop = new StopWords();
    private ArrayList<StringFreq> sorted = new ArrayList<>();
    private double topWords;
    private StringJoiner stringJoiner;

    public Summary(File input,
                   double percentageOfTopwords,
                   double percentageOfVideo,
                   int lookAhead,
                   int cutOffValue) throws Exception {


        initializeHistogram(input);
//        srtReader(input);
        clean(histogram, tf, df);
        createSorted(percentageOfTopwords);

        this.topWords = percentageOfTopwords;
        for (Range range : histogram) {
            for (int j = 0; j < sorted.size(); j++) {
                if (range.counts.containsKey(sorted.get(j).word)) {
                    range.importance += range.counts.get(sorted.get(j).word);
                }
            }
        }
        int totalCount = 0;
        int currentLength = 0;
        boolean inWord = false;

        ArrayList<Group> groups = new ArrayList<>();
        Group group = null;
        int histogramSize = histogram.size();
        for (int i = 0; i < histogramSize; i++) {
            Range range = histogram.get(i);
            if (range.importance > cutOffValue) {
                if (!inWord) {
                    inWord = true;
                    group = new Group();
                    group.add(range);
                } else {
                    group.add(range);
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
        System.out.println("Number of groups: " + groups.size());
//        Collections.sort(groups);
        stringJoiner = new StringJoiner("\n");
        groups.forEach(e -> stringJoiner.add(e.toString()));
//        System.out.println(stringJoiner);
//        for (Group group1 : groups) {
//            System.out.println(group1.get(0).startTime + " - " + group1.get(group1.size() - 1).startTime);
//            stringJoiner.add(group1.get(0).startTime + " - " + group1.get(group1.size() - 1).startTime);
//            System.out.println(group1.print() + "\n" + group1.totalImportance);
//            System.out.println();
//        }
//
//        String[] lastTimeStamp = histogram.get(histogramSize - 1).startTime.split(":");
//        int videoLength = Integer.parseInt(lastTimeStamp[0]);
//        double outputLength = (double) videoLength * percentageOfVideo;
//        int outputtedSoFar = 0;
//        for (Group g : groups) {
//            if (outputtedSoFar < outputLength) {
//                System.out.println(g.get(0).startTime + " - " + g.get(g.size() - 1).startTime);
//                outputtedSoFar += g.groupLength();
//            } else {
//                break;
//            }
//        }
    }

    public Summary(String input,
                   double percentageOfTopwords,
                   double percentageOfVideo,
                   int lookAhead,
                   int cutOffValue) {


        initializeHistogram(input);
        clean(histogram, tf, df);
        createSorted(percentageOfTopwords);

        this.topWords = percentageOfTopwords;
        for (Range range : histogram) {
            for (int j = 0; j < sorted.size(); j++) {
                if (range.counts.containsKey(sorted.get(j).word)) {
                    range.importance += range.counts.get(sorted.get(j).word);
                }
            }
        }
        boolean inWord = false;

        ArrayList<Group> groups = new ArrayList<>();
        Group group = null;
        int histogramSize = histogram.size();
        for (int i = 0; i < histogramSize; i++) {
            Range range = histogram.get(i);
            if (range.importance > cutOffValue) {
                if (!inWord) {
                    inWord = true;
                    group = new Group();
                    group.add(range);
                } else {
                    group.add(range);
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
        System.out.println("Number of groups: " + groups.size());
        stringJoiner = new StringJoiner("\n");
        groups.forEach(e -> stringJoiner.add(e.toString()));
    }

    private static void srtReader(File srtFile) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(srtFile))) {
            String currentLine;
            String[] timeStamps;
            StringJoiner stringJoiner;
            int index = 0;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.matches(".\\d+")) {
                    index = Integer.parseInt(currentLine.substring(1)) - 1;
                    continue;
                }
                if (currentLine.matches("\\d+")) {
                    index = Integer.parseInt(currentLine) - 1;
                    continue;
                }
                if (currentLine.contains("-->")) {
                    timeStamps = currentLine.split(" --> ");
                    stringJoiner = new StringJoiner(" ");
                    while ((currentLine = bufferedReader.readLine()) != null) {
                        if (currentLine.matches("\\s*")) {
                            break;
                        }
                        if (!currentLine.contains("<font")) {
                            stringJoiner.add(currentLine);
                        }
                    }
                    histogram.add(new Range(timeStamps[0], timeStamps[1], stringJoiner.toString(), index));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        File input = new File("PacketSwitching");
//        try {
//            new Summary(input, 0.25, 0.25, 0, 2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * creates histogram of ranges, initialized with times and string contents, but counts
     * field remains uninitialized
     */
    private void initializeHistogram(File input) throws Exception {
        Scanner reader = new Scanner(input);
        String decider = reader.nextLine();
        reader = new Scanner(input);
        int counter = 0;
        if (decider.matches("^[0-9]*:[0-9]*$")) {
            while (reader.hasNextLine()) {
                String time = reader.nextLine();
                String contents = reader.nextLine();
                if (time.matches("^[0-9]:.*$")) {
                    time = "0" + time;
                }
                histogram.add(new Range(time, contents, counter++));
            }
        }
        //if video is over an hour , this will not work
        else {
            while (reader.hasNextLine()) {
                String[] divisions = reader.nextLine().split(":");
                String time = divisions[0] + ":" + divisions[1].substring(0, 2);
                if (time.matches("^[0-9]:.*$")) {
                    time = "0" + time;
                }
                String contents = divisions[1].substring(2, divisions[1].length());
                for (int i = 2; i < divisions.length; i++)
                    contents += " " + divisions[i];
                histogram.add(new Range(time, contents, counter++));
            }
        }
    }

    private void initializeHistogram(String input) {
        String[] split = input.split("\n");
        int counter = 0;
        if (split[0].matches("^[0-9]*:[0-9]*$")) {
            for (int i = 0; i < split.length; i = i + 2) {
                String time = split[i];
                String contents = split[i + 1];
                if (time.matches("^[0-9]:.*$")) {
                    time = "0" + time;
                }
                histogram.add(new Range(time, contents, counter++));
            }
        } else {
            //if video is over an hour , this will not work
            for (int i = 0; i < split.length; i++) {
                String[] divisions = split[i].split(":");
                String time = divisions[0] + ":" + divisions[1].substring(0, 2);
                if (time.matches("^[0-9]:.*$")) {
                    time = "0" + time;
                }
                String contents = divisions[1].substring(2, divisions[1].length());
                for (int j = 2; i < divisions.length; j++)
                    contents += " " + divisions[j];
                histogram.add(new Range(time, contents, counter++));
            }
        }
    }

    /**
     * will take the histogram and update each range's count field
     * will also initialize the tf HashMap
     *
     * @param input should be the histogram of values
     */

    private void clean(ArrayList<Range> input, HashMap<String, Double> tf, HashMap<String, Double> df) {
        for (Range range : input) {
            //if (current.contents.matches("^[A-Z]*:.*"))
            String[] words = range.contents.toLowerCase().replaceAll("[^\\w ]", "").split("\\s+");
            HashMap<String, Double> holder = new HashMap<>();
            for (String currentWord : words) {
                if (stop.isStopWord(currentWord)) {
                    continue;
                }
                //stem the current word
                Stemmer s = new Stemmer();
                char[] word = currentWord.toCharArray();
                s.add(word, word.length);
                s.stem();
                String toAdd = s.toString();

                if (!holder.containsKey(toAdd)) {
                    holder.put(toAdd, 1.0);
                    if (!df.containsKey(toAdd)) {
                        df.put(toAdd, 1.0);
                    } else {
                        df.put(toAdd, df.get(toAdd) + 1);
                    }
                } else
                    holder.put(toAdd, holder.get(toAdd) + 1);

                if (!tf.containsKey(toAdd))
                    tf.put(toAdd, 1.0);
                else
                    tf.put(toAdd, tf.get(toAdd) + 1);
            }
            holder.remove("");
            range.counts = holder;
        }
        tf.remove("");
        for (Range range : input) {
            for (String s : range.counts.keySet()) {
                range.counts.put(s, tf_idf(s, tf, df));
                tfidf.put(s, tf_idf(s, tf, df));
            }
        }
    }

    private double tf_idf(String t, HashMap<String, Double> tf, HashMap<String, Double> df) {
        double tfd = 1 + java.lang.Math.log10(tf.get(t)); //log normalized tf
        double idfd = Math.log10(histogram.size() / df.get(t)); //inverse freq
        return tfd * idfd;
    }

    private void createSorted(double percentageOfTopWords) {
        ArrayList<StringFreq> temp = new ArrayList<>();
        for (String s : tf.keySet()) {
            temp.add(new StringFreq(s, tf.get(s)));
        }
        Collections.sort(temp);
        for (int i = (int) (temp.size() * (1 - percentageOfTopWords)); i < temp.size(); i++) {
            sorted.add(new StringFreq(temp.get(i).word, tfidf.get(temp.get(i).word)));
        }
        Collections.sort(sorted);
    }

    @Override
    public String toString() {
        return stringJoiner.toString();
    }
}
