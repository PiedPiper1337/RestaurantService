package controllers;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.openqa.selenium.WebDriver;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import utils.Constants;
import utils.StringManip;
import views.html.video;

import javax.inject.Inject;
import javax.inject.Singleton;

public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();

    @Inject
    @Singleton
    private static WebDriver webDriver;


    @With(IPAction.class)
    public Result index() {
        logger.trace("index method called");
        return ok(views.html.index.render());
    }


    public Result genericFailure(String badResource) {
        logger.trace("bad url attempt at {}", badResource);
        return badRequest("This page does not exist.");
    }


    /**
     * takes a url with v parameter
     * checks if the url is directly inside or inside an entire url
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
        } else if (videoId.contains("youtube.com")) {
            videoId = StringManip.extractParameter(videoId, "v");
        }
        logger.debug("returning actual video string");
        String videoURLToEmbed = Constants.EMBED_URL + videoId;
        logger.debug("video url is: {}", videoURLToEmbed);
        return ok(video.render(videoURLToEmbed));
    }

    public Result getSummarization() {

        Graph<Object, Object> testGraph = new SimpleGraph<Object, Object>(DefaultEdge.class);
        logger.debug("Yay I made a graph to fulfill A5 requirements!");

        return ok("Here you go..");
    }



}
