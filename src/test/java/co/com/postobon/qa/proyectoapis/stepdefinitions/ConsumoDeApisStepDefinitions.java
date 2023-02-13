package co.com.postobon.qa.proyectoapis.stepdefinitions;

import co.com.postobon.qa.proyectoapis.questions.LastResponseStatusCode;
import co.com.postobon.qa.proyectoapis.questions.Valida;
import co.com.postobon.qa.proyectoapis.tasks.Consumo;
import co.com.postobon.qa.proyectoapis.tasks.GuardarToken;
import co.com.postobon.qa.proyectoapis.utils.Utils;
import co.com.postobon.qa.proyectoapis.utils.ValidacionesDB;
import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.es.Cuando;
import cucumber.api.java.es.Dado;
import cucumber.api.java.es.Entonces;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.GivenWhenThen;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.actors.OnStage;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static co.com.postobon.qa.proyectoapis.utils.Utils.*;
import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.*;
import static org.hamcrest.Matchers.equalTo;

public class ConsumoDeApisStepDefinitions {

    private EnvironmentVariables environmentVariables;

    @Before
    public void setStage() {
        setTheStage(new Cast());
    }

    @Dado("^quiero ejecutar el API (.+) con la peticion (.+)$")
    public void quieroEjecutarElAPIConLaPeticion(String api, String peticion) {
    }

    @Cuando("^consumo el api (.+) con la data data de prueba$")
    public void consumoElApiConLaDataDePrueba(String api, DataTable datosPeticion) throws IOException {
        List<String> datos = datosPeticion.topCells();
        String url, body, metodo;
        String[] pathFile, nameKeys;
        //Generar token
        //OnStage.theActorCalled("Token").wasAbleTo(
        //        Consumo.elApi("TOKEN",null,null,null,null,null));
        //System.out.println("Value token");
        //System.out.println(SerenityRest.lastResponse().path("access_token").toString());

        //Preparar data para el api
        url = environmentVariables.getProperty(datos.get(0)) + datos.get(1);
        body = datos.get(4);

        pathFile = datos.get(5).split("#");
        nameKeys = datos.get(6).split("#");


        metodo = datos.get(2);
        System.out.println(url);
        Map<String, String> headers = new HashMap<>();
        for (String pair : datos.get(3).split(",")) {
            String[] entry = pair.split(":");
            if (entry[0].contains("Authorization")) {
                //headers.put(entry[0].trim(), "Bearer " + SerenityRest.lastResponse().path("access_token").toString());

                String strToken = new String(Files.readAllBytes(Paths.get("src/test/resources/datadriven/token.txt")));

                // headers.put(entry[0].trim(), "Bearer " + SerenityRest.lastResponse().body().asString());
                headers.put(entry[0].trim(), "Bearer " + strToken);
            } else {
                headers.put(entry[0].trim(), entry[1].trim());
            }
        }

        //Consumir API
        theActorCalled("Operador/ambos").wasAbleTo(
                Consumo.elApi(metodo, url, headers, body, pathFile, nameKeys));
    }

    @Entonces("^verifico el status code (\\d+)$")
    public void verificoElStatusCode(int sc) {
        System.out.println(sc);
        theActorInTheSpotlight().should(seeThat("El status code: ", LastResponseStatusCode.is(), equalTo(sc)));
        //Utils.Imprimir(Serenity.sessionVariableCalled("Respuesta"));
    }

    @Entonces("^las respuestas esperadas (.+) en las rutas (.+) del response$")
    public void lasRespuestasEsperadasEnLasRutasDelResponse(String respuestasEsperadas, String rutas) {
        System.out.println(respuestasEsperadas);
        System.out.println(rutas);
        String[] respuestasE, rutasList;
        respuestasE = respuestasEsperadas.split("#");
        rutasList = rutas.split("#");
        List<String> re;
        List<String> ru;
        re = Arrays.asList(respuestasE);
        ru = Arrays.asList(rutasList);
        theActorInTheSpotlight().should(seeThat("Respuesta esperada", Valida.bodyRespuesta(ru), equalTo(re)));
        Imprimir(Serenity.sessionVariableCalled("Respuesta"));
        Serenity.setSessionVariable("Respuesta").to(null);

    }

    @Entonces("^guardo el token en archivo$")
    public void guardo() {
        theActorCalled("Operador/ambos").wasAbleTo(
                GuardarToken.guardar());

    }

}
