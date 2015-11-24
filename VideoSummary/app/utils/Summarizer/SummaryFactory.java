package utils.Summarizer;

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
}
