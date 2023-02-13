package co.com.postobon.qa.proyectoapis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;


public class ConexionAS400 {
    //Variables Necesarias Bello
    private String strHost = "172.26.4.30";
    private String strUsuario = "CERCACHC";
    private String strPass = "Pruebas123";
    //Variables Necesarias Central
    private String strHostCentral = "172.26.4.31";
    private String strUsuarioCentral = "CERCACHC";
    private String strPassCentral = "Pruebas123";
    private Connection conexion;


    private final Logger LOGGER = LoggerFactory.getLogger(ConexionSqlServer.class);

    //Se Puede usar un Archivo Properties
    /*
     private void cargarDatos() {
		try {
			Properties propiedad= new Properties();
			propiedad.load(new FileInputStream("src/test/resources/dataInicioAS400.properties"));
			strUsuario = propiedad.getProperty("Usuario");
			strPass = propiedad.getProperty("Pass");
			strHost = propiedad.getProperty("Host");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
    */

    public Connection conectarme() {
        //cargarDatos();
        try {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            conexion = DriverManager.getConnection("jdbc:as400://" + strHost, strUsuario, strPass);
            LOGGER.info("La conexión a la BD de AS400 Bello fue exitosa");
        } catch (ClassNotFoundException ex) {
            System.err.println("Error Cargando la Clase " + toString() + "\n" + ex.getMessage());
        } catch (SQLException ex) {
            System.err.println("Error intentando crear la conexion " + toString() + "\n" + ex.getMessage());
            LOGGER.info("Se volverá a realizar la conexión a la BD de AS400 Bello");
            int intentos = 1;
            while (intentos <= 30) {
                try {
                    Class.forName("com.ibm.as400.access.AS400JDBCDriver");
                } catch (ClassNotFoundException e) {
                    System.err.println("Error Cargando la Clase " + toString() + "\n" + ex.getMessage());
                }
                try {
                    conexion = DriverManager.getConnection("jdbc:as400://" + strHost, strUsuario, strPass);
                    intentos = 31;
                    LOGGER.info("La conexión a la BD de AS400 Bello fue exitosa");
                } catch (SQLException e) {
                    LOGGER.info("No se pudo realizar la conexión a la BD de AS400 Bello");
                }
                intentos++;
            }
        }
        return this.conexion;
    }

    public Connection conectarmeCentral() {
        //cargarDatos();
        try {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            conexion = DriverManager.getConnection("jdbc:as400://" + strHostCentral, strUsuarioCentral, strPassCentral);
            LOGGER.info("La conexión a la BD de AS400 Central fue exitosa");
        } catch (ClassNotFoundException ex) {
            System.err.println("Error Cargando la Clase " + toString() + "\n" + ex.getMessage());
        } catch (SQLException ex) {
            System.err.println("Error intentando crear la conexion " + toString() + "\n" + ex.getMessage());
            LOGGER.info("Se volverá a realizar la conexión a la BD de AS400 Central");
            int intentos = 1;
            while (intentos <= 30) {
                try {
                    Class.forName("com.ibm.as400.access.AS400JDBCDriver");
                } catch (ClassNotFoundException e) {
                    System.err.println("Error Cargando la Clase " + toString() + "\n" + ex.getMessage());
                }
                try {
                    conexion = DriverManager.getConnection("jdbc:as400://" + strHost, strUsuario, strPass);
                    intentos = 31;
                    LOGGER.info("La conexión a la BD de AS400 Central fue exitosa");
                } catch (SQLException e) {
                    LOGGER.info("No se pudo realizar la conexión a la BD de AS400 Central");
                }
                intentos++;
            }
        }
        return this.conexion;
    }

    public ResultSet Consultar(String sql) throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conectarme();
        }
        Statement st = conexion.createStatement();
        return st.executeQuery(sql);
    }

    public ResultSet consultarCentral(String sql) throws  SQLException {
        if (conexion == null || conexion.isClosed()) {
            conectarmeCentral();
        }
        Statement st = conexion.createStatement();
        return st.executeQuery(sql);
    }

    public void close() throws SQLException {
        if (this.conexion != null) {
            this.conexion.close();
        }
    }

}
