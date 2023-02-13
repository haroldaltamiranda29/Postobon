package co.com.postobon.qa.proyectoapis.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;

public class SmallPdfPage {

    private SmallPdfPage() {
    }

    public static final Target BUTTON_LOAD = Target.the("boton para abrir menu SVFE")
            .locatedBy("//*[@id='wrapper']/section[1]/div/file-selector/div/div/div[2]/div/div[3]"); 
    public static final Target BUTTON_LOAD_ADOBE = Target.the("boton para abrir menu SVFE")
            .locatedBy("//*[@id='lifecycle-nativebutton']"); 
    public static final Target INPUT_TYPE_FILE = Target.the("boton para abrir menu SVBO")
            .locatedBy("//input[@type='file']");
}
