package utils;

import utils.Summarizer.Group;
import utils.Summarizer.Summary;

import java.util.ArrayList;

/**
 * Created by brianzhao on 11/3/15.
 */
public class SummaryGenerator {
    public static ArrayList<Group> generate(String videoId) {
        String transcript = TranscriptGenerator.getTranscript(videoId);
        if (transcript == null) {
            return null;
        }
        Summary summary = new Summary(transcript);
        ArrayList<Group> summaryGroups = summary.generateSummary();
        return summaryGroups;
    }
}
