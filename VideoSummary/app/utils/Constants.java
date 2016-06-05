package utils;

import utils.SummaryUtils.SummaryTools.Weight;

/**
 * Created by brianzhao on 10/13/15.
 */
public class Constants {
    //URL related constants
    public static final String EMBED_URL = "https://www.youtube.com/embed/";
    public static final String YOUTUBE_WATCH_URL = "https://www.youtube.com/watch/";
    public static final String BASE_YOUTUBE_URL = "http://www.youtube.com/";
    public static final String WATCH_URL_APPEND = "watch?v=";


    //transcript related constants
    public static final String TIME_REGION_DELIMITER = "---";
    public static final double PROPORTION_OF_PUNCTUATION_CUTOFF = 0.017;
    public static final int BROWSER_RETRIES = 2;


    //summary related constants
    public static final double DEFAULT_PERCENTAGE_TOPWORDS = 0.25;
    public static final Weight DEFAULT_WEIGHT_TYPE = Weight.TFIDF;
    public static final double DEFAULT_SUMMARY_DURATION_SECONDS = 180;
    public static final double DEFAULT_SUMMARY_PROPORTION = 0.25;
    public static final boolean DEFAULT_NORMALIZE_ON_DURATION = false;
    public static final double DEFAULT_CUTOFF = 0.15;
    public static final int WORD_CLOUD_SIZE = 100;
    public static final double WORD_CLOUD_FAKE_MAX_IMPORTANCE = 2000.0;

    //cache related constants
    public static final int CACHE_TIME = 15 * 60; //15 minutes

    //html related constants
    public static final String ERROR_404 = "Oops! 404 Not Found.";
    public static final String ERROR_USER_BAD_URL = "Looks like this isn't a valid Youtube URL!";
    public static final String ERROR_INTERNAL_SERVER_EXCEPTION = "Oops! Sorry, we had an internal error!";

    //internal system stress related constants
    public static final int NUM_CONCURRENT_VIDEO_INFO_RETRIEVAL_ACTORS = 20;
}
