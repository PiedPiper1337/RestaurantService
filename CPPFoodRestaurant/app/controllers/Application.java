package controllers;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {
    private static final org.slf4j.Logger logger = Logger.of(Application.class).underlying();

    public Result aliens() {
        logger.trace("Aliens! OoOoOoOoOoOo!!!");
        return ok(index.render("Aliens! OoOoOoOoOoOo!!!"));
    }

    public Result index() {
        logger.trace("index method called");
        return ok(index.render("Your new application is ready."));
    }
}
