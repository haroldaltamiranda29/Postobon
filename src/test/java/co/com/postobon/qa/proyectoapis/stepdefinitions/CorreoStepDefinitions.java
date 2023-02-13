package co.com.postobon.qa.proyectoapis.stepdefinitions;

import co.com.postobon.qa.proyectoapis.tasks.Correo;
import cucumber.api.java.es.Dado;
import net.serenitybdd.screenplay.actors.OnStage;

public class CorreoStepDefinitions {



	
	@Dado("^el consumo del correo electronico$")
	public void elConsumoDelCorreoElectronico() {
		 OnStage.theActorInTheSpotlight().attemptsTo(
	                Correo.consumirCorreo()
	        );		
	}


}
