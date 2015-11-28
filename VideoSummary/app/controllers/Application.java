package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import play.Logger;
import play.cache.CacheApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import utils.Constants;
import utils.Pipeline;
import utils.StringManip;
import utils.Summarizer.*;
import views.html.video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * HOW TO DEBUG USING PLAY FRAMEWORK + INTELLIJ: https://www.playframework.com/documentation/2.4.x/IDE
 */
public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();

    @Inject
    CacheApi cache;

    /**
     * TODO FIX having entirely separate html and css page for mobile
     * @return
     */
    @With(IPAction.class)
    public Result index() {
        logger.trace("index method called");
        /**
         * support for returning a different mobile page
         */
//
//        String[] agent = request().headers().get("user-agent");
//        if (agent.length < 1) {
//            return ok(views.html.index.render());
//        } else {
//            if (isMobileDevice(agent[0])) {
//                return ok(views.html.mobileIndex.render());
//            } else {
//                return ok(views.html.index.render());
//            }
//        }
        return ok(views.html.index.render());
    }

    /**
     * we should replace this with a fail whale picture when we deploy
     * this html template was obtained from: http://bootsnipp.com/snippets/featured/simple-404-not-found-page
     * authored by: BhaumikPatel
     * <p>
     * TODO make this better, I hacked together some formatting using html tutorial from first link of google search
     *
     * @param badResource
     * @return
     */
    public Result genericFailure(String badResource) {
        logger.trace("bad url attempt at {}", badResource);
        return notFound(views.html.errorPage.render(Constants.ERROR_404));
    }

    public Result blank() {
        return ok();
    }

    public Result summaryTimes(String videoId) {
        logger.debug("got a POST");
        try {
            String result = cache.get(videoId);
            if (result == null) {
                Summary summaryResult = SummaryFactory.generateBasicSummary(videoId);
                if (summaryResult == null) {
                    return internalServerError("Sorry but we had an error processing your video");
                }
                Map returnObject = new HashMap();

                List<Group> summaryGroups = summaryResult.generateSummary();
                List<JsonNode> jsonNodes = new ArrayList<>();
                for (Group summary : summaryGroups) {
                    jsonNodes.add(summary.getJson());
                }

                returnObject.put("Groups", jsonNodes);
                returnObject.put("WordCloud", summaryResult.generateWordCloud());
                returnObject.put("Histogram", summaryResult.histogram());

                result = Json.toJson(returnObject).toString();
                cache.set(videoId, result, Constants.CACHE_TIME);
            }
            return ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return internalServerError(views.html.errorPage.render(Constants.ERROR_INTERNAL_SERVER_EXCEPTION));
        }
    }

    /**
     * takes a http request with a parameter
     * "v" parameter means somebody appended "sum" to a youtube link
     * "url" parameter means the request came from our home page
     * displays youtube video
     *
     * @return
     */
    public Result displayVideo() {
        try {
            String url = request().getQueryString("url");
            if (url != null) {
                if (StringManip.isFullUrl(url)) {
                    logger.debug("The youtube url was: {}", url);
                    String videoId = StringManip.extractParameter(url, "v");
                    return ok(video.render(videoId));
                } else {
                    logger.debug("Received a malformed youtube url: {}", url);
                    return badRequest(views.html.errorPage.render(Constants.ERROR_USER_BAD_URL));
                }
            } else {
                String videoID = request().getQueryString("v");
                if (videoID != null) {
                    logger.debug("Got a redirect from youtube.com url. The video ID is: {}", videoID);
                    return ok(video.render(videoID));
                } else {
                    logger.debug("Received a bad url attempt at /watch with incorrect query string");
                    return notFound("Sorry, but there's nothing here!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            return internalServerError(views.html.errorPage.render(Constants.ERROR_INTERNAL_SERVER_EXCEPTION));
        }
    }

    public Result displayTranscript() {
        logger.trace("transcript method called");
        String vParameter = request().getQueryString("v");
        if (vParameter == null) {
            logger.debug("video query was null, redirecting to index");
//            return redirect(controllers.routes.Application.index());
            return redirect("/");
        }
        Transcript transcript = TranscriptFactory.getTranscript(vParameter);
        if (transcript == null) {
            return internalServerError("Sorry but we had an error processing your video");
        }
        return ok(transcript.toString());
    }

    public Result runNLP() throws Exception {
        logger.debug("Processing aScandalInBohemia");
        String title = "NLPData/aScandalInBohemia";
        String analyzed = Pipeline.pos(title);
        return ok(analyzed);
    }

    public Result summarize() {
        logger.trace("Summarization");
        String videoId = request().getQueryString("v");
        if (videoId == null) {
            logger.debug("summarization parameter query was null, redirecting to index");
            return redirect("/");
        }
        Summary summaryResult = SummaryFactory.generateBasicSummary(videoId);
        if (summaryResult == null) {
            return internalServerError("Sorry but we had an error processing your video");
        }
        List<Group> summaryGroups = summaryResult.generateSummary();

        cache.set(videoId, Json.toJson(summaryGroups).toString(), Constants.CACHE_TIME);
        return ok(summaryGroups.toString());
    }

    public static boolean isMobileDevice(String userAgent) {
        String lowerCaseUserAgent = userAgent.toLowerCase();
        return lowerCaseUserAgent.contains("android")
                || lowerCaseUserAgent.contains("iphone")
                || lowerCaseUserAgent.contains("ipad");
    }
}
