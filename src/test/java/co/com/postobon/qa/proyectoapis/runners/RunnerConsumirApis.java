package co.com.postobon.qa.proyectoapis.runners;

import co.com.postobon.qa.proyectoapis.utils.BeforeSuite;
import co.com.postobon.qa.proyectoapis.utils.DataToFeature;
import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.runner.RunWith;

import java.io.IOException;

@CucumberOptions(
        features = "src/test/resources/features/consumo_de_apis.feature",
        glue = "co.com.postobon.qa.proyectoapis.stepdefinitions",
        //tags = "@PostToken",
        snippets = SnippetType.CAMELCASE )
@RunWith(RunnerPersonalizado.class)
public class RunnerConsumirApis {
    @BeforeSuite
    public static void test() throws InvalidFormatException, IOException {
     DataToFeature.overrideFeatureFiles("src/test/resources/features/");
    }
}
