package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import play.Logger;
import play.cache.CacheApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import utils.*;
import utils.Summarizer.Group;
import utils.Summarizer.Summary;
import views.html.video;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

//import utils.Summarizer.Summary;

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
        String result = cache.get(videoId);
        if (result == null) {
            ArrayList<Group> summaryGroups = SummaryGenerator.generate(videoId);
            if (summaryGroups == null) {
                return internalServerError("Sorry but we had an error trying to process your video");
            } else {
                result = Json.toJson(summaryGroups).toString();
                cache.set(videoId, result, Constants.CACHE_TIME);
            }
        }
        return ok(result);
    }


    /**
     * takes a http request with v parameter
     * checks if the url is directly inside 'v' or inside an entire url
     * displays youtube video
     *
     * @return
     */
    public Result displayVideo() {
        logger.trace("demo method called");
        String videoId = request().getQueryString("v");
        if (videoId == null) {
            logger.debug("video query was null, redirecting to index");
//            return redirect(controllers.routes.Application.index());
            return redirect("/");
        }

        if (StringManip.isFullUrl(videoId)) {
            videoId = StringManip.extractParameter(videoId, "v");
        }

        logger.debug("returning actual video string");
        String videoURLToEmbed = videoId; //Constants.EMBED_URL + videoId;
        logger.debug("video url is: {}", videoURLToEmbed);
        return ok(video.render(videoURLToEmbed));
    }

    public Result displayTranscript() {
        logger.trace("transcript method called");
        String vParameter = request().getQueryString("v");
        if (vParameter == null) {
            logger.debug("video query was null, redirecting to index");
//            return redirect(controllers.routes.Application.index());
            return redirect("/");
        }
        String transcript = TranscriptGenerator.getTranscript(vParameter);
        if (transcript == null) {
            return internalServerError("Sorry but we had an error processing your video");
        }
        return ok(transcript);
    }

    public Result getSummarization() {

        Graph<Object, Object> testGraph = new SimpleGraph<>(DefaultEdge.class);
        logger.debug("Yay I made a graph to fulfill A5 requirements!");
        return ok("Here you go..");
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

        ArrayList<Group> summaryGroups = SummaryGenerator.generate(videoId);
        if (summaryGroups== null) {
            return internalServerError("Sorry but we had an error processing your video");
        }
        cache.set(videoId, Json.toJson(summaryGroups).toString(), Constants.CACHE_TIME);
        return ok(summaryGroups.toString());
    }

}
