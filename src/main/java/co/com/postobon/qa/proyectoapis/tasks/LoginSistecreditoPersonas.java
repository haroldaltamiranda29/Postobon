package co.com.postobon.qa.proyectoapis.tasks;

import co.com.postobon.qa.proyectoapis.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.postobon.qa.proyectoapis.userinterfaces.SitecreditoSolicitudCreditoPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;

public class LoginSistecreditoPersonas implements Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginSistecreditoPersonas.class);
    private String user;										 
    private String pass;
    
    @Override
    public <T extends Actor> void performAs(T actor) {   	
    	
        String s = Utils.formatiarAviso("Logeandose en el aplicativo SmartVista con usuario: " + user + " y contraseña: " + pass);
        LOGGER.info(s);
        actor.attemptsTo(
                Click.on(SitecreditoSolicitudCreditoPage.BTN_INICIAR_SESION)
        );
        
        //Aqui se hacia el cambio, pero se debe llevar a un metodo o interaccion que realice el cambio
        
        Utils.esperaExplicita(3000L);
    }
}
