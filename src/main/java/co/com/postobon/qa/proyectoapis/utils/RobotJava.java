package co.com.postobon.qa.proyectoapis.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotJava {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotJava.class);
	
	public static void cargarRutaVentanaCarga(String strRuta) {
		 try {
			Robot robot = new Robot();
			Utils.esperaExplicita(3000L);
			//Copia y Pega la ruta 
			StringSelection stringSelection = new StringSelection(strRuta);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, stringSelection);
		
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);			
		} catch (AWTException e) {
			LOGGER.info(e.getMessage());
		}
	}
	
}
