import play.Application;
import play.GlobalSettings;
import play.Logger;

/**
 * Created by brianzhao on 10/11/15.
 */
public class Global extends GlobalSettings {
    private static final org.slf4j.Logger logger = Logger.of(Global.class).underlying();
    @Override
    public void onStart(Application application) {
        logger.trace("Application has started");
    }

    @Override
    public void onStop(Application application) {
        logger.trace("Application shutdown...");
    }
}
