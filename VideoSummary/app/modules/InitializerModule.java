package modules;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;

import scala.collection.Seq;

/**
 * Created by brianzhao on 10/25/15.
 */
public class InitializerModule extends Module {
    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(
                bind(WebDriver.class, ChromeDriver.class)
        );
    }

}
