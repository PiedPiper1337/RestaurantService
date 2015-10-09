package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();

    public Result index() {
        logger.trace("index method called");
        return ok(index.render("Your new application is ready."));
    }

    

}
