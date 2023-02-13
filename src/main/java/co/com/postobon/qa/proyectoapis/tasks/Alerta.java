package co.com.postobon.qa.proyectoapis.tasks;


import java.io.File;

import co.com.postobon.qa.proyectoapis.utils.Utils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.postobon.qa.proyectoapis.userinterfaces.SitecreditoSolicitudCreditoPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.thucydides.core.annotations.Managed;

public class Alerta  implements  Performable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Alerta.class);
    File file = new File("src/test/resources/files/prueba.txt");
    @Managed                                                            
    WebDriver driver;
    @Override
    public <T extends Actor> void performAs(T actor) {
        String s = Utils.formatiarAviso("yendo a process session log");
        LOGGER.info(s);

        	actor.attemptsTo(               
        			 Enter.keyValues("juanjovane5@yopmail.com").into(SitecreditoSolicitudCreditoPage.INPUT_USUARIO),
        			 Enter.keyValues("123456").into(SitecreditoSolicitudCreditoPage.INPUT_CLAVE),
        			 Click.on(SitecreditoSolicitudCreditoPage.BTN_INICIAR_SESION),
        			 Enter.keyValues("568574").into(SitecreditoSolicitudCreditoPage.INPUT_IDENTIDAD),
        			 Enter.keyValues("568574").into(SitecreditoSolicitudCreditoPage.INPUT_VERIFICAR_IDENTIDAD),
        			 Click.on(SitecreditoSolicitudCreditoPage.BTN_ENVIAR)
        );
        
        	Utils.esperaExplicita(3000L);        	
         	actor.attemptsTo(                    
       			 Click.on(SitecreditoSolicitudCreditoPage.BTN_ACEPTAR),
       			 Click.on(SitecreditoSolicitudCreditoPage.BTN_SOLICITUD)
       );        
         	 Utils.esperaExplicita(5000L);
         	 
    }
}
