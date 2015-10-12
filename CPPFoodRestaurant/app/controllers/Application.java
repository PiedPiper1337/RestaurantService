package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.HashMap;

public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();

    public Result index() {
        logger.trace("index method called");
        return ok(index.render("Your new application is ready."));
    }

    /**
     * example json return method
     *
     * @return
     */
    public Result helloWorld() {
        logger.trace("hello world method called");
        HashMap javaObject = new HashMap();
        javaObject.put("Message", "Hello World");
        javaObject.put("Here is the key", "Here is the value");
        JsonNode json = Json.toJson(javaObject);
        logger.debug("message that is sent back by the server: {}", json.toString());
        return ok(json.toString());
    }

    public Result login() {
        logger.trace("login method called");
        return ok(index.render("This is the login page"));
    }

    public Result aliens() {
        logger.trace("Aliens! OoOoOoOoOoOo!!!");
        return ok(index.render("Aliens! OoOoOoOoOoOo!!!"));
    }
}
