package co.com.postobon.qa.proyectoapis.stepdefinitions;

import co.com.postobon.qa.proyectoapis.tasks.GoTo;
import cucumber.api.java.es.Cuando;
import net.serenitybdd.screenplay.actors.OnStage;

public class ManejoAlertaStepDefinitions {

	@Cuando("^ejecuto accion sobre alert js$")
	public void ejecutoAccionSobreAlertJs() {		
		 OnStage.theActorInTheSpotlight().attemptsTo(
	                GoTo.manejoAlertas()
	        );		
	}
}
