package co.com.postobon.qa.proyectoapis.interactions;

import java.util.Set;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Switch;

public class SwitchToLastWindow implements Performable {
    @Override
    public <T extends Actor> void performAs(T actor) {
        String lastWindow = "";
        Set<String> windowHandles = Serenity.getWebdriverManager().getCurrentDriver().getWindowHandles();

        for (String window : windowHandles) {
            lastWindow = window;
        }
        Switch.toWindow(lastWindow);        
    }
}
