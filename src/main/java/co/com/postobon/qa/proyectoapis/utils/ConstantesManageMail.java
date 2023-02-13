package co.com.postobon.qa.proyectoapis.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.postobon.qa.proyectoapis.exceptions.TestError;

public class ConstantesManageMail {


    private ConstantesManageMail() {
    }
    
	   public static final String PROTOCOLO = "imaps";
	   public static final String PROVEEDOR_CORREO_GMAIL = "gmail";
	   public static final String PROVEEDOR_CORREO_OFFICE365 = "office365";
	   public static final String PROVEEDOR_CORREO_OUTLOOK = "hotmail";
	   public static final String SERVIDOR_ENTRANTE_GMAIL = "imap.gmail.com";
	   public static final String SERVIDOR_ENTRANTE_OFFICE365 = "outlook.office365.com";
	   public static final String SERVIDOR_ENTRANTE_OUTLOOK = "imap.outlook.com";
	   public static final String FOLDER_INBOX = "INBOX";


	  
	   
	   public static final String ERROR = "Error";
	   public static final String MOTORAS400="as400";
	   public static final String MOTORSQLSERVER="sqlserver";
	   public static String Proveedor = "Error";
	   
	   
	   
	   private static final String RUTA_PROPERTIES = "src/main/resources/config.properties";
	    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);
	    private static final Properties PROPERTIES = loadProperties();
	   
	    public static String USUARIO_CORREO= PROPERTIES.getProperty("UsuarioMail");
	    public static String CLAVE_CORREO= PROPERTIES.getProperty("ClaveMail");
		 

	    
		   
	    public static Properties loadProperties() {
	    	
	        Properties properties = new Properties();
	        try {
	            String s = Utils.formatiarAviso("Cargando datos de config.properties");
	            LOGGER.info(s);
	            properties.load(new FileInputStream(RUTA_PROPERTIES));
	        } catch (IOException e) {
	            String s = Utils.formatiarAviso("Error al abrir el archivo properties config.properties");
	            throw new TestError(s, e.getCause());
	        }
	        return properties;
	    }
	
}
