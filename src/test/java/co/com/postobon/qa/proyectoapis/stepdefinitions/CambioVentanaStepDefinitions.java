package co.com.postobon.qa.proyectoapis.stepdefinitions;

import co.com.postobon.qa.proyectoapis.tasks.Login;
import co.com.postobon.qa.proyectoapis.interactions.SwitchTo;
import cucumber.api.java.ast.Cuando;
import net.serenitybdd.screenplay.actors.OnStage;


public class CambioVentanaStepDefinitions {

	@Cuando("^ingreso a iniciar sesion en personas$")
	public void ingresoAIniciarSesionEnPersonas() {
		 OnStage.theActorInTheSpotlight().attemptsTo(
		Login.sistecreditoPersonas(),
		SwitchTo.lastWindowName("CredinetPersonasFrontWebPortal")
		);
	}
		    
}
