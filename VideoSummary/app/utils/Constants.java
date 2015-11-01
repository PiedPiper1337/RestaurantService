package utils;

import utils.Summarizer.Weight;

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


    //summary related constants
    public static final double DEFAULT_PERCENTAGE_TOPWORDS = 0.25;
    public static final Weight DEFAULT_WEIGHT_TYPE = Weight.TFIDF;
    public static final double DEFAULT_SUMMARY_DURATION_SECONDS = 180;
    public static final double DEFAULT_SUMMARY_PROPORTION = 0.25;
    public static final boolean DEFAULT_NORMALIZE_ON_DURATION = true;

}
