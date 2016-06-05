package utils.SummaryUtils.SummaryTools;

import utils.SummaryUtils.Summarizers.SimpleFrequencySummary;
import utils.SummaryUtils.Summarizers.SubsumSummary;

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
        Transcript transcript = TranscriptFactory.getTranscript(videoId);
        if (transcript == null) {
            return null;
        }
        return new SimpleFrequencySummary(transcript);
    }

    public static Summary generateSubsumSummary(String videoId) {
        Transcript transcript = TranscriptFactory.getTranscript(videoId);
        if (transcript == null) {
            return null;
        }
        return new SubsumSummary(transcript);
    }
}
