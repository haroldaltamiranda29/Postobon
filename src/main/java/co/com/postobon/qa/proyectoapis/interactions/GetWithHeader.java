package co.com.postobon.qa.proyectoapis.interactions;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.RestInteraction;
import net.thucydides.core.annotations.Step;

import java.util.Map;

public class GetWithHeader extends RestInteraction {

    private Map<String, Object> headers;
    private String resource;

    public GetWithHeader(String resource, Map<String, Object> headers) {
        this.headers = headers;
        this.resource = resource;
    }

    @Step("{0} executes a GET on the resource #resource")
    @Override
    public <T extends Actor> void performAs(T actor) {
        rest().contentType(ContentType.JSON)
                .relaxedHTTPSValidation()
                .headers(headers)
                .when()
                .get(resource);
    }
}
