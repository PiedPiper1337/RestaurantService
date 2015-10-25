package controllers;

import org.openqa.selenium.WebDriver;
import utils.Constants;
import utils.StringManip;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import utils.TranscriptGenerator;
import views.html.video;

import javax.inject.Inject;
import javax.inject.Singleton;

public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();

    @Inject
    private WebDriver webDriver;


    @With(IPAction.class)

    public Result index() {
        logger.trace("index method called");
//        return ok(views.html.index.render());
//        TranscriptGenerator.getTranscriptFromFullURL(null);
        return ok(webDriver.toString());
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


//
//    /**
//     * example json return method
//     *
//     * @return
//     */
//    public Result helloWorld() {
//        logger.trace("hello world method called");
//        HashMap<String,String> javaObject = new HashMap<>();
//        javaObject.put("Message", "Hello World");
//        javaObject.put("Here is the key", "Here is the value");
//        JsonNode json = Json.toJson(javaObject);
//        logger.debug("message that is sent back by the server: {}", json.toString());
//        return ok(json.toString());
//    }

//    public Result login() {
//        logger.trace("login method called");
//        return ok(index.render("This is the login page"));
//    }

//    public Result aliens() {
//        logger.trace("Aliens! OoOoOoOoOoOo!!!");
//        return ok(index.render("Aliens! OoOoOoOoOoOo!!!"));
//    }

}
