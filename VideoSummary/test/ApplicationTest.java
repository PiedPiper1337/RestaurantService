import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Content;
import views.html.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;


/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 */
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertEquals(2, a);
    }

    @Test
    public void renderTemplate() {
//        Content html = (Content) index.render("Your new application is ready.");
//        assertEquals("text/html", contentType(html));
//        assertTrue(contentAsString(html).contains("Your new application is ready."));
    }

    @Test
    public void requiredTest() {
        Result result = new controllers.Application().index();
        assertEquals(OK, result.status());
    }
}
