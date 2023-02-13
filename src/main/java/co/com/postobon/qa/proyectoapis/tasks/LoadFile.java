package co.com.postobon.qa.proyectoapis.tasks;


import java.io.File;

import co.com.postobon.qa.proyectoapis.utils.RobotJava;
import co.com.postobon.qa.proyectoapis.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.postobon.qa.proyectoapis.userinterfaces.SmallPdfPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;

public class LoadFile implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFile.class);
    File file = new File("src/test/resources/files/prueba.txt");
    @Override
    public <T extends Actor> void performAs(T actor) {
        String s = Utils.formatiarAviso("yendo a process session log");
        LOGGER.info(s);
     
        actor.attemptsTo(                
        		//METODO 1.
        		//Tener en cuenta que la carga solo funcionará Si el elemento al que envía un archivo debe tener el formato <input type="file">	
        		//No Necesita dar Click, setea el valor directamente
        		//Archivo En los Recursos del Proyecto 
        		 //Enter.keyValues(file.getAbsolutePath()).into(SmallPdfPage.INPUT_TYPE_FILE)
        		//Archivo en Ubicacion Local en Maquina
                //Enter.keyValues("C:\\Users\\yruizm\\OneDrive - Choucair Testing\\Documentos\\prueba.txt").into(SmallPdfPage.INPUT_TYPE_FILE)
        		
        		
        );
        
        
        
        
    	//METODO 2 IMPLEMENTANDO METODO ROBOT.
        actor.attemptsTo(                
        		//METODO 2 IMPLEMENTANDO METODO ROBOT.
        		//Click Boton para Abrir ventana de carga        		
        		 Click.on(SmallPdfPage.BUTTON_LOAD_ADOBE)        		
        );
          //Archivo en Ubicacion Local en Maquina
         RobotJava.cargarRutaVentanaCarga("C:\\Users\\yruizm\\OneDrive - Choucair Testing\\Documentos\\prueba.txt");
   		 //Archivo En los Recursos del Proyecto 
         //RobotJava robotJava = null;
        //RobotJava.cargarRutaVentanaCarga(file.getAbsolutePath());
   	 
        
        Utils.esperaExplicita(5000L);
    }
}
