package co.com.postobon.qa.proyectoapis.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleDriver;

/*
 ***Para Prueba Local***	
import java.util.logging.Level;
import java.util.logging.Logger;*/


public class ConexionOracle {

	/*Variables de Ejemplo
    private final String USUARIO = "gpalermo";
    private final String PASS = "gp1l3rm0";
    private final String SID = "pruadv";
    private final String HOST = "oraclecont06.empresa.com.co";
    private final int PUERTO = 1532;*/
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void registrarDriver() throws SQLException {
        OracleDriver oracleDriver = new oracle.jdbc.driver.OracleDriver();
        DriverManager.registerDriver(oracleDriver);
    }

    public void conectar(String strHost, Integer strPuerto, String strSID, String strUsuario, String strContrasena) throws SQLException {
        if (connection == null || connection.isClosed() == true) {
            String cadenaConexion = "jdbc:oracle:thin:@" + strHost + ":" + strPuerto + "/" + strSID;
            registrarDriver();
            connection = DriverManager.getConnection(cadenaConexion, strUsuario, strContrasena);
        }
    }

    public void cerrar() throws SQLException {
        if (connection != null && connection.isClosed() == false) {
            connection.close();
        }
    }      
}