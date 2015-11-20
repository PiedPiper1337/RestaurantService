package utils;

import utils.Summarizer.Group;
import utils.Summarizer.SimpleFrequencySummary;
import utils.Summarizer.Summary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brianzhao on 11/3/15.
 */
public class SummaryFactory {
    /**
     *
     * @param videoId
     * @return
     */
    public static Summary generateBasicSummary(String videoId) {
        String transcript = TranscriptGenerator.getTranscript(videoId);
        if (transcript == null) {
            return null;
        }
        return new SimpleFrequencySummary(transcript);
    }
}
