package utils;

import org.openqa.selenium.firefox.FirefoxDriver;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;

/**
 * Created by brianzhao on 11/21/15.
 */
public class FirefoxDriverCustom extends FirefoxDriver {
    @Inject
    public FirefoxDriverCustom(ApplicationLifecycle lifecycle) {
        lifecycle.addStopHook(() -> {
            this.quit();
            return F.Promise.pure(null);
        });
    }
}
