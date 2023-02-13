package co.com.postobon.qa.proyectoapis.tasks;

import net.serenitybdd.screenplay.Performable;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GoTo {

    private GoTo() {
    }
   
    public static Performable loadFile() {
        return instrumented(LoadFile.class);
    }
    
    public static Performable manejoAlertas() {
        return instrumented(Alerta.class);
    }


}
