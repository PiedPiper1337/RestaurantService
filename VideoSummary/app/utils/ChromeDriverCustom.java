package utils;

import org.openqa.selenium.chrome.ChromeDriver;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by brianzhao on 10/27/15.
 * https://www.playframework.com/documentation/2.4.x/JavaDependencyInjection#Stopping/cleaning-up
 */

@Singleton
public class ChromeDriverCustom extends ChromeDriver {
    @Inject
    public ChromeDriverCustom(ApplicationLifecycle lifecycle) {
        lifecycle.addStopHook(() -> {
            this.quit();
            return F.Promise.pure(null);
        });
    }
}
