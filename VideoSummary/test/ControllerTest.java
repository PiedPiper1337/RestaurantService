import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.Logger;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import utils.TranscriptGenerator;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

/**
 * https://stackoverflow.com/questions/31509026/play-2-4-how-do-i-disable-routes-file-loading-during-unit-tests]
 * https://www.playframework.com/documentation/2.4.x/JavaFunctionalTest
 * https://www.playframework.com/documentation/2.4.x/JavaTest
 */

public class ControllerTest extends WithApplication {
    private static final org.slf4j.Logger logger = Logger.of(ControllerTest.class).underlying();
    @Inject
    Application application;

    @Before
    public void setup() {
        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
                // Install custom test binding here
                String osName = System.getProperty("os.name").toLowerCase();
                logger.debug("Detected operating system: {}", osName);
                if (osName.startsWith("mac")) {
                    System.setProperty("webdriver.chrome.driver", "chromedriverMac");
                } else if (osName.startsWith("window")) {
                    //do nothing for now
                } else {
                    System.setProperty("webdriver.chrome.driver", "chromedriverLinux");
                }
                logger.debug("chromedriver environment path set");
                requestStaticInjection(TranscriptGenerator.class);
                bind(WebDriver.class).to(ChromeDriver.class).asEagerSingleton();
            }
        };

        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .overrides(testModule)
                .configure("play.http.router", "my.test.Routes");
        Guice.createInjector(builder.applicationModule()).injectMembers(this);
    }

    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");
        Result result = route(request);
        assertEquals(OK, result.status());
    }

    @After
    public void teardown() {
        Helpers.stop(application);
    }

}