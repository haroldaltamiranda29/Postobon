package co.com.postobon.qa.proyectoapis.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;

/**
 * Una clase para para conectarse a Correos Electronicos y obtener mensajes y archivos.
 * @version 1.0, 16/09/2021
 * @author Yefri Ruiz Mosquera
 */
public class ManageMail {

	private final static Properties properties = new Properties();
	private int puerto;
	private static  String host; 
	private static String usuario;
	private static String clave; 
	private static Session session;
	static String strTextoCorreo = null;


	private static void init(String strProveedorCorreo) { 
		properties.put("mail.imap.starttls.enable", "false");
		properties.put("mail.imap.socketFactory.class","javax.net.ssl.SSLSocketFactory" );
		properties.put("mail.imap.socketFactory.fallback", "false");
		properties.put("mail.imap.port", "993");
		properties.put("mail.imap.socketFactory.port", "993");
		properties.put("mail.imap.partialfetch", "false");
		
		session = Session.getDefaultInstance(properties);
		session.setDebug(false);
		
	

		switch (strProveedorCorreo) {
		case "gmail":
			host=ConstantesManageMail.SERVIDOR_ENTRANTE_GMAIL;
			break;

		case "hotmail":
			host=ConstantesManageMail.SERVIDOR_ENTRANTE_OUTLOOK;
			break;
		default:
			host=ConstantesManageMail.SERVIDOR_ENTRANTE_OFFICE365;
			break;
		}
		
		ConstantesManageMail.loadProperties();
		usuario=ConstantesManageMail.USUARIO_CORREO;
		clave=ConstantesManageMail.CLAVE_CORREO;

	}
	


	
	  /** 
     * Metodo que obtiene y devuelve el Texto del Ultimo Mensaje que se recibio desde un correo Electronico Especifico.
     * @param strProveedorCorreo Proveedor de Correo Electronico al que se conecta para extraer el mensaje.
     * @param strMailEnvia Correo Electronico del que se recibio el mensaje.
     * <br>Si contiene archivos en formatos ( .pptx, .xls, .txt, .pdf, .doc, .png, .jpg, .jpeg) los decarga en la ruta del proyecto .../src/test/resources/files/download
     * @return Texto contenido en el mensaje.
     */
	
	public static String ObtenerUltimoMensaje(String strProveedorCorreo, String strMailEnvia) throws MessagingException {
		init(strProveedorCorreo);
		
		Store store = session.getStore("imap");
		store.connect(host,usuario,clave);
		//Folder
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);

		    final SearchTerm filterA = new FromTerm(new InternetAddress(strMailEnvia)); //Quien lo Envia
	        final SearchTerm[] filters = {filterA };
	      
	        final SearchTerm searchTerm = new AndTerm(filters);
	        final Message[] messages = folder.search(searchTerm);
	        
	        Message msn=messages[messages.length-1];
	        	        
	        try {
	        	
				   SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
				   DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");			  
				
					Date date = sdf.parse(msn.getSentDate().toString());
					String formattedDate = targetFormat.format(date); 
					  
					strTextoCorreo=getTextMessageSSS(msn);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        		
	    store.close();
		return strTextoCorreo;
	}
	
	
	  /** 
     * Metodo que obtiene y devuelve el Texto del Ultimo Mensaje que se recibio desde un correo Electronico Especifico.
     * @param strProveedorCorreo Proveedor de Correo Electronico al que se conecta para extraer el mensaje.
     * @param strMailEnvia Correo Electronico del que se recibio el mensaje.
     * @param strAsunto Asunto del Correo Electronico que se recibio.
     * <br>Si contiene archivos en formatos ( .pptx, .xls, .txt, .pdf, .doc, .png, .jpg, .jpeg) los decarga en la ruta del proyecto .../src/test/resources/files/download
     * @return Texto contenido en el mensaje.
     */
	public static String ObtenerUltimoMensajeConAsunto(String strProveedorCorreo, String strMailEnvia, String strAsunto) throws MessagingException {
		init(strProveedorCorreo);
	
		Store store = session.getStore("imap");
		store.connect(host,usuario,clave);
		//Folder
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		
			final SearchTerm filterA = new FromTerm(new InternetAddress(strMailEnvia)); //Quien lo Envia
	        final SearchTerm[] filters = {filterA };
	      
	        final SearchTerm searchTerm = new AndTerm(filters);
	        final Message[] messages = folder.search(searchTerm);
	        int indice=99999999;
	        System.out.println("messages.length: "+ messages.length);
	        for (int i=0;i<messages.length;i++)
			{
	        	if (messages[i].getSubject().contentEquals(strAsunto)) {					 
	        		indice=i;
				}
			}	
	        
	        if (indice!=99999999) {

			   SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			   DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");			  
			try {
				   Date date = sdf.parse(messages[indice].getSentDate().toString());
				   String formattedDate = targetFormat.format(date); 
				   strTextoCorreo=getTextMessageSSS(messages[indice]);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
			} else {
				strTextoCorreo="CORREO NO ENCONTRADO.";
			}
	        
		
	        		
	    store.close();
		return strTextoCorreo;
		
	}
	
	
	private static String getTextMessageSSS(Message msn) throws MessagingException, IOException {
	    String result = "";
	    try {
	    	 if (msn.isMimeType("text/plain")) {
	 	        result = msn.getContent().toString();
	 	    } else if (msn.isMimeType("multipart/*")) {
	 	    	// Obtenemos el contenido, que es de tipo MultiPart.
	 	        MimeMultipart mimeMultipart = (MimeMultipart) msn.getContent();
	 	        result = getTextFromMimeMultipart(mimeMultipart);
	 	    }
		} catch (Exception e) {
			//Se lanza Exception y validamos si es que tiene certificado digital y el mensaje es de -Unable to load BODYSTRUCTURE- 
		    if (msn instanceof MimeMessage && "Unable to load BODYSTRUCTURE".equalsIgnoreCase(e.getMessage())) {
		    	new MimeMessage((MimeMessage) msn).getContent();
		    	//creating local copy of given MimeMessage
	            MimeMessage msgDownloaded = new MimeMessage((MimeMessage) msn);
		    	if (msgDownloaded.isMimeType("multipart/*")) {
		    		// Obtenemos el contenido, que es de tipo MultiPart.
		 	        MimeMultipart mimeMultipart = (MimeMultipart) msgDownloaded.getContent();
		 	        result = getTextFromMimeMultipart(mimeMultipart);
		    	}		    	 
		    }
		}
	   
	    DescargarArchivos(msn);
	    
	    return result;
	}	
	
	
	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
	    String result = "";
	    BodyPart bodyPart;
	    int count = mimeMultipart.getCount();
	    for (int i = 0; i < count; i++) {
	        bodyPart = mimeMultipart.getBodyPart(i);
	        
	        if (bodyPart.isMimeType("text/plain")) {
	            result = result + "\n" + bodyPart.getContent();
	            break;
	        } 
	        else if (bodyPart.isMimeType("text/html")) {
	            String html = (String) bodyPart.getContent();
	            result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
	        } 
	        else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }        
	    }	    
	    return result;
	}	
	
	
	
	private static String DescargarArchivos(Message msn)  throws MessagingException, IOException{
	    String strNombreArchivoAdjunto= "";
	    File directorios = new File(System.getProperty("user.dir")+"/src/test/resources/files/download");
	    boolean blnAdjuntoValido;
	    
	    Object content = msn.getContent(); 
	    
	    if (content instanceof String) return null; 
	    if (content instanceof Multipart) { 
	    	Multipart multipart = (Multipart) content; 
	    	List<InputStream> result = new ArrayList<InputStream>(); 
	    	for (int i = 0; i < multipart.getCount(); i++) 
	    	{ 
	    		   if (multipart.getBodyPart(i).getFileName()!=null) {
	    			   strNombreArchivoAdjunto=multipart.getBodyPart(i).getFileName().toString();
				}
	    		  
	    		   blnAdjuntoValido=strNombreArchivoAdjunto.contains(".pptx") | strNombreArchivoAdjunto.contains(".xls") | 
		    			   strNombreArchivoAdjunto.contains(".txt")  | strNombreArchivoAdjunto.contains(".pdf") | 
		    			   strNombreArchivoAdjunto.contains(".doc")  | strNombreArchivoAdjunto.contains(".png") | 
		    			   strNombreArchivoAdjunto.contains(".jpg")  | strNombreArchivoAdjunto.contains(".jpeg");
	    		   
	    		   if (blnAdjuntoValido==true){
	    			   System.out.println("EXPROPIESE DEL CORREO. "+strNombreArchivoAdjunto);
	    			  //El nombre que se guardaaqui es donde quitamos caracteres invalidos
	    			   strNombreArchivoAdjunto=reemplazarCaracteresEspeciales(strNombreArchivoAdjunto);  
	    		        if (!directorios.exists()) {
	    		            if (directorios.mkdirs()) {
	    		                System.out.println("Multiples directorios fueron creados");
	    		            }
	    		        }
	    			   FileOutputStream fichero = new FileOutputStream(directorios+"/"+strNombreArchivoAdjunto);	    			    
	    			   InputStream imagen = multipart.getBodyPart(i).getInputStream();
	    			   
	    			   byte [] bytes = new byte[1000];
	    			   int leidos=0;
	    			   while ((leidos=imagen.read(bytes))>0)
	    			   {
	    			      fichero.write(bytes,0,leidos);
	    			   }	    			   
				}
	    		   
	    	}
	    }

		 System.out.println("No contiene archivos");
	    
	    return "No contiene archivos";
	}	
	
	private static String reemplazarCaracteresEspeciales(String palabra) {
        String[] caracteresMalos =  {"ñ","|","à","á","À","Á","è","é","È","É","ì","í","Ì","Í","ò","ó","Ò","Ó","ù","ú","Ù","Ú","\b","/",":","<","*","?",">","=","$", "@"};
        String[] caracteresBuenos = {"n","_","a","a","A","A","e","e","E","E","i","i","I","I","o","o","O","O","u","u","U","U",  "", "", "", "", "", "", "", "", "",  ""};

        for (String letraMala : caracteresMalos) {
            if(palabra.contains(letraMala)){
                palabra = palabra.replace(letraMala,caracteresBuenos[Arrays.asList(caracteresMalos).indexOf(letraMala)]);
            }
        }

        return palabra;

    }
	
}
