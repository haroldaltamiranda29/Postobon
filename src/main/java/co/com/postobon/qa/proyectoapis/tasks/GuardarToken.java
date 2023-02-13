package co.com.postobon.qa.proyectoapis.tasks;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class GuardarToken implements Task {

    String metodo, url, body;
    String []pathFile,nameKeys;
    Map<String, String> headers;


    public static GuardarToken guardar() {
        return Tasks.instrumented(GuardarToken.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {


        Response lastResponse = SerenityRest.lastResponse();
        String strToken=lastResponse.body().asString();
        try {
            String ruta = "src/test/resources/datadriven/token.txt";
            File file = new File(ruta);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(strToken);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
