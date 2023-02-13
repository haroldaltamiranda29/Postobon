package co.com.postobon.qa.proyectoapis.interactions;

import java.util.Set;

import co.com.postobon.qa.proyectoapis.utils.Utils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class SwitchToNameWindow implements Performable {
	
	String NombreVentana;
	private SwitchToNameWindow (String NombreVentana) {
		this.NombreVentana=NombreVentana;
	}
	
    @Override
    public <T extends Actor> void performAs(T actor) {
        /*String lastWindow = "";
        Set<String> windowHandles = Serenity.getWebdriverManager().getCurrentDriver().getWindowHandles();

        for (String window : windowHandles) {
            lastWindow = window;
        }
        Switch.toWindow(lastWindow);        
        */
        
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        

        Set<String> allHandles = driver.getWindowHandles();
        //log.debug("open windows: " + allHandles.size());
        if (allHandles.size() < 2) {
            //log.error("Not enough windows open!");
            return;
        }
        
        
        String firstHandle = driver.getWindowHandle();
        Dimension windowSize = driver.manage().window().getSize();

 

        //log.debug("current window: " + driver.getTitle() + ", at: " + driver.getCurrentUrl());
        boolean found = false;
        for (String handle : allHandles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contentEquals(NombreVentana)) {
                found = true;
                break;
            }
        }
        if (!found) {
            //log.error("Could not find window with title: '" + title + "'");
            driver.switchTo().window(firstHandle);
        }
 
        Utils.esperaExplicita(10000L);
    }
}
