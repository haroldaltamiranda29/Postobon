package co.com.postobon.qa.proyectoapis.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.RestInteraction;

import java.util.Map;

public class Delete extends RestInteraction {

    private String resource;
    private String body;
    private Map<String, Object> headers;


    public Delete(String resource, String body, Map<String, Object> headers) {
        this.resource = resource;
        this.body = body;
        this.headers = headers;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        rest().relaxedHTTPSValidation()
                .body(body).headers(headers)
                .when().delete(resource)
        ;
    }
}
