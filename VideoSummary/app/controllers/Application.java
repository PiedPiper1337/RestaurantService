package controllers;

import models.YoutubeVideo;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import utils.Constants;
import utils.StringManip;
import utils.TranscriptGenerator;
import views.html.video;

public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();


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

        String videoId = StringManip.isFullUrl(vParameter) ? StringManip.getVideoId(vParameter) : vParameter;
        String transcript;
        YoutubeVideo youtubeVideo = YoutubeVideo.find.where().eq("videoId", videoId).findUnique();
        if (youtubeVideo == null) {
            logger.debug("video hasn't been seen before");
            transcript = TranscriptGenerator.getTranscriptFromVideoID(videoId);
            youtubeVideo = new YoutubeVideo(videoId, transcript);
            youtubeVideo.save();
        } else if (youtubeVideo.getTranscript() == null) {
            logger.debug("filling in nonexistent transcript");
            transcript = TranscriptGenerator.getTranscriptFromVideoID(videoId);
            youtubeVideo.setTranscript(transcript);
            youtubeVideo.save();
        } else {
            logger.debug("using database transcript");
            transcript = youtubeVideo.getTranscript();
        }

        return ok(transcript);
    }

    public Result getSummarization() {

        Graph<Object, Object> testGraph = new SimpleGraph<Object, Object>(DefaultEdge.class);
        logger.debug("Yay I made a graph to fulfill A5 requirements!");

        return ok("Here you go..");
    }

}
