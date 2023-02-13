package co.com.postobon.qa.proyectoapis.tasks;

import javax.mail.MessagingException;

import co.com.postobon.qa.proyectoapis.utils.ConstantesManageMail;
import co.com.postobon.qa.proyectoapis.utils.ManageMail;
import co.com.postobon.qa.proyectoapis.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class ConsumoCorreo implements Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumoCorreo.class);
    private String user;										 
    private String pass;
    ManageMail manageMail;
    
    @Override
    public <T extends Actor> void performAs(T actor) {   	
    	
        String s = Utils.formatiarAviso("Logeandose en el aplicativo SmartVista con usuario: " + user + " y contraseña: " + pass);
        LOGGER.info(s);


        
        
        try {

			
        	String textValidacion=ManageMail.ObtenerUltimoMensaje(ConstantesManageMail.PROVEEDOR_CORREO_GMAIL, "yefri.ruiz.mosquera@hotmail.com");
        	System.out.println(textValidacion);
		
        	
        	
        	textValidacion=ManageMail.ObtenerUltimoMensajeConAsunto(ConstantesManageMail.PROVEEDOR_CORREO_GMAIL, "yruizm@choucairtesting.com", "Correo Prueba JavaMail");
        	System.out.println(textValidacion);
		
        	
        	
		
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        
        
        
        
        //Aqui se hacia el cambio, pero se debe llevar a un metodo o interaccion que realice el cambio
        
        Utils.esperaExplicita(10000L);
    }
}
