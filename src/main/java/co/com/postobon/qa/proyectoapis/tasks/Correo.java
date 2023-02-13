package co.com.postobon.qa.proyectoapis.tasks;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Tasks;

public class Correo {

	private Correo() {
	}

	public static Performable consumirCorreo() {
		return Tasks.instrumented(ConsumoCorreo.class);
	}

}
