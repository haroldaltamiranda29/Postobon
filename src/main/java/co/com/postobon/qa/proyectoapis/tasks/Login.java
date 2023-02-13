package co.com.postobon.qa.proyectoapis.tasks;

import net.serenitybdd.screenplay.Performable;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Login {

    private Login() {
    }
    
    public static Performable sistecreditoPersonas() {
        return instrumented(LoginSistecreditoPersonas.class);
    }
}
