package controllers;

import Utils.*;

import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.video;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();

    public Result index() {
        logger.trace("index method called");
        return ok(index.render());
    }

    /**
     * takes a url with v parameter
     * checks if the url is directly inside or inside an entire url
     * displays youtube video
     * @return
     */
    public Result demo() {
        logger.trace("demo method called");
        String videoId = request().getQueryString("v");
        if (videoId == null) {
            logger.debug("video query was null, redirecting to index");
            return redirect(controllers.routes.Application.index());
        }else if (videoId.contains("youtube.com")) {
            videoId =StringManipulation.extractParameter(videoId, "v");
        }
        logger.debug("returning actual video string");
        String videoURLToEmbed = Constants.EMBED_URL + videoId;
        logger.debug("video url is: {}", videoURLToEmbed);
        return ok(video.render(videoURLToEmbed));
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
