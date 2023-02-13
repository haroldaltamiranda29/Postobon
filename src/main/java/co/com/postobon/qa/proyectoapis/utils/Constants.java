package co.com.postobon.qa.proyectoapis.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.com.postobon.qa.proyectoapis.exceptions.TestError;

public class Constants<codigosAB, stactic> {

    private Constants() {
    }

    private static final String RUTA_PROPERTIES = "src/main/resources/config.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);
    private static final Properties PROPERTIES = loadProperties();
    public static final String LINK_SMALL_PDF = PROPERTIES.getProperty("urlSmallPdf");
 
    private static Properties loadProperties() {
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
