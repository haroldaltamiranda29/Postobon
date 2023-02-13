package co.com.postobon.qa.proyectoapis.utils;

import cucumber.api.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

public class BeforeHook {

    @Before
    public void prepareStage() {
          OnStage.setTheStage(new OnlineCast());
          //Elimina Todos los Archivos
           //Utils.cleanFiles();
    }
}
