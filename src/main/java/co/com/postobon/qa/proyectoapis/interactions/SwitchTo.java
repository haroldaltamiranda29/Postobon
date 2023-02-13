package co.com.postobon.qa.proyectoapis.interactions;

import net.serenitybdd.screenplay.Performable;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class SwitchTo {

    private SwitchTo() {
    }

    public static Performable lastWindow() {
        return instrumented(SwitchToLastWindow.class);
    }

    public static Performable lastWindowName(String NombreVentana) {
    	//Aqui se puede implementar la clase de Santi de cambio de Ventana
        return instrumented(SwitchToNameWindow.class, NombreVentana);
    }
}
