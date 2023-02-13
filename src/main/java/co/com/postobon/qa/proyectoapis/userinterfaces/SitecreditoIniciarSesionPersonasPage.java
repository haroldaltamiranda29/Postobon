package co.com.postobon.qa.proyectoapis.userinterfaces;

import net.serenitybdd.screenplay.targets.Target;

public class SitecreditoIniciarSesionPersonasPage {


    private SitecreditoIniciarSesionPersonasPage() {
    }

    
    
    public static final Target INPUT_USUARIO = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='username']");
    
    
    public static final Target INPUT_CLAVE = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='password']");

    
    public static final Target BTN_INICIAR_SESION = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("/html/body/app-root/div/div/app-home/div/div/div/div[2]/div[2]/div[2]/app-form-login/form/div[5]/button");
    
    
    public static final Target INPUT_IDENTIDAD = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='nid']");
    
    
    public static final Target INPUT_VERIFICAR_IDENTIDAD= Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='verify_nid']");

    
    public static final Target BTN_ENVIAR = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("/html/body/app-root/div/div/app-register-request/div/div/div/app-form-register-request/div/div/form/div[4]/div/div[2]/button");
    
    
    
    public static final Target BTN_ACEPTAR = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("/html/body/app-root/div/div/app-dashboard/div/app-quick-access/div[2]/div/div/span/button");
    
    
    
    public static final Target BTN_SOLICITUD = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("/html/body/app-root/div/div/app-dashboard/div/app-quick-access/div/div/a[1]");
    
    
    
    
    
    
    
    

    public static final Target INPUT_DOCUMENTO = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='ctl00_ContentPlaceHolder1_txtNumDocumento']");
    
    
    public static final Target INPUT_NOMBRES = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@name='ctl00$ContentPlaceHolder1$txtNombres']");
    
    
    public static final Target INPUT_APELLIDOS = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@name='ctl00$ContentPlaceHolder1$txtApellidos']");
    

    public static final Target SELECT_DIA_NACIMIENTO= Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='txt_dia']");
    public static final Target SELECT_MES_NACIMIENTO = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='dobmonth']");    
    public static final Target SELECT_AÑO_NACIMIENTO = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='txt_anio']");


    
    public static final Target INPUT_TELEFONO = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='txtTelefono']");
    
    public static final Target INPUT_CORREO = Target.the("campo para ingresar el nombre de usuario")
    		.locatedBy("//*[@id='txtEmail']");
    
    public static final Target BUTTON_CLAUSULA = Target.the("boton para iniciar sesión")
            .locatedBy("//*[@id='chkTerminos']"); 
    
    public static final Target BUTTON_CONTINUAR= Target.the("boton para iniciar sesión")
    		.locatedBy("//*[@name='btnTemplate']");
    
    public static final Target BUTTON_Cerrar= Target.the("boton para iniciar sesión")
            .locatedBy("/html/body/div[3]/div/div[2]/div[1]"); 
    
}
