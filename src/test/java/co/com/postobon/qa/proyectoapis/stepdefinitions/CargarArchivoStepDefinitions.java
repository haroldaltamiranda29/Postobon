package co.com.postobon.qa.proyectoapis.stepdefinitions;


import co.com.postobon.qa.proyectoapis.tasks.GoTo;
import co.com.postobon.qa.proyectoapis.tasks.Open;
import cucumber.api.java.es.Cuando;
import cucumber.api.java.es.Dado;
import net.serenitybdd.screenplay.actors.OnStage;

public class CargarArchivoStepDefinitions {
	@Dado("^el ingreso al sitio web (.*)$")
	public void elIngresoAlSitioWeb(String strUrl) {
		 OnStage.theActorCalled("ActorSistecredito").wasAbleTo(
	             Open.theWebSite(strUrl)
	     );
	}
	
	@Cuando("^cargo el archivo$")
	public void cargoElArchivoConLa() {
		 OnStage.theActorInTheSpotlight().attemptsTo(
	                GoTo.loadFile()
	        );		
	}
}
