package co.com.postobon.qa.proyectoapis.utils.enums;

public enum EnumsSiSeNecesitan {

    CEDULA("01"),
    TARJETA_IDENTIDAD("02"),
    PASAPORTE("03");

    private String codigoIdentificacion;
	
    private EnumsSiSeNecesitan(String codigoIdentificacion) {
        this.codigoIdentificacion = codigoIdentificacion;
    }



    public String getAba() {
        return codigoIdentificacion;
    }
}
