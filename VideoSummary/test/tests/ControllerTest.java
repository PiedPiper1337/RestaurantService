package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

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
//    @Inject
    Application application;

    @Before
    public void setup() {
//        Module testModule = new AbstractModule() {
//            @Override
//            public void configure() {
//                // Install custom test binding here
//                String osName = System.getProperty("os.name").toLowerCase();
//                logger.debug("Detected operating system: {}", osName);
//                if (osName.startsWith("mac")) {
//                    System.setProperty("webdriver.chrome.driver", "chromedriverMac");
//                } else if (osName.startsWith("window")) {
//                    //do nothing for now
//                } else {
//                    System.setProperty("webdriver.chrome.driver", "chromedriverLinux");
//                }
//                logger.debug("chromedriver environment path set");
//                requestStaticInjection(TranscriptFactory.class);
//                bind(WebDriver.class).to(utils.ChromeDriverCustom.class).asEagerSingleton();
//            }
//        };
//
//        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
//                .builder(new ApplicationLoader.Context(Environment.simple()))
//                .overrides(testModule)
//                .configure("play.http.router", "my.test.Routes");
//        Guice.createInjector(builder.applicationModule()).injectMembers(this);
        application = Helpers.fakeApplication();
        Helpers.start(application);
        logger.trace("\n\n\t\t\t\t\t\t\t\t\t*****************************************************");
        logger.trace("\t\t\tStarting a new test...");
    }

    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");
        Result result = route(request);
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType());
    }

    @Test
    public void testRandom() {
        assertEquals(1+1,2);
    }

    @Test
    public void testDiplayTranscript() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/transcript");

        Result result = route(request);
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType());
    }

    @After
    public void teardown() {
        Helpers.stop(application);
//        logger.trace("\t\t\tFinished a test...");
        logger.trace("\t\t\tFinished a test...\n\t\t\t\t\t\t\t\t\t*****************************************************\n\n");
    }

}