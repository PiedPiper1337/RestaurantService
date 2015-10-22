package controllers;

import play.Logger;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created by brianzhao on 10/20/15.
 */
public class IPAction extends play.mvc.Action.Simple {
    private static final org.slf4j.Logger logger = Logger.of(IPAction.class).underlying();

    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        logger.debug(ctx.request().remoteAddress());
        return delegate.call(ctx);
    }
}
