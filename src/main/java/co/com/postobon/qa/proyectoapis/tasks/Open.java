package co.com.postobon.qa.proyectoapis.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Tasks;

public class Open {

	private Open() {
	}

	public static Performable theWebSite(String strUrl) {
		return Tasks.instrumented(OpenTheWebSitePage.class, strUrl);
	}

}
