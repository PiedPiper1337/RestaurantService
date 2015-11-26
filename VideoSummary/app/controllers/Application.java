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
import java.util.List;


/**
 * HOW TO DEBUG USING PLAY FRAMEWORK + INTELLIJ: https://www.playframework.com/documentation/2.4.x/IDE
 */
public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();

    @Inject
    CacheApi cache;

    @With(IPAction.class)
    public Result index() {
        logger.trace("index method called");
        return ok(views.html.index.render());
    }

    /**
     * we should replace this with a fail whale picture when we deploy
     *
     * @param badResource
     * @return
     */
    public Result genericFailure(String badResource) {
        logger.trace("bad url attempt at {}", badResource);
        return badRequest("This page does not exist.");
    }

    public Result blank() {
        return ok();
    }

    public Result summaryTimes(String videoId) {
        logger.debug("got a POST");
        String result = cache.get(videoId);
        if (result == null) {
            Summary summaryResult = SummaryFactory.generateBasicSummary(videoId);
            if (summaryResult == null) {
                return internalServerError("Sorry but we had an error processing your video");
            }
            List<Group> summaryGroups = summaryResult.generateSummary();

            ArrayList<JsonNode> jsonNodes = new ArrayList<>();
            for (Group summary : summaryGroups) {
                jsonNodes.add(summary.getJson());
            }
            result = Json.toJson(jsonNodes).toString();
            cache.set(videoId, result, Constants.CACHE_TIME);
        }
        return ok(result);
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
        String url = request().getQueryString("url");
        if (url != null) {
            logger.debug("The youtube url was: {}", url);
            //TODO do a regex check, return failure if not matching youtube url syntax
            if (StringManip.isFullUrl(url)) {
                String videoId = StringManip.extractParameter(url, "v");
                return ok(video.render(videoId));
            } else {
                return badRequest("nope dude");
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






//        String videoId = request().getQueryString("v");
//        if (videoId == null) {
//            logger.debug("video query was null, redirecting to index");
////            return redirect(controllers.routes.Application.index());
//            return redirect("/");
//        }
//
//
//
//        String videoURLToEmbed = videoId; //Constants.EMBED_URL + videoId;
//        logger.debug("The video ID is: {}", videoURLToEmbed);
//        return ok(video.render(videoURLToEmbed));
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

}
