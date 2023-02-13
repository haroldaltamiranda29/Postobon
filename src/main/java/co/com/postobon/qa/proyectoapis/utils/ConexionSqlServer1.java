package co.com.postobon.qa.proyectoapis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;


public class ConexionSqlServer1 {


    //SQLSERVER
    private String strHostSqlServer = "sfvbdqa.gaseosas.net";
    private String strUserSqlServer = "UserQAB_rmcaro";
    private String strPasswordSqlServer = "gMqtjS68BHhMETIDmJPO";
    private int intPortSqlServer = 1433;
    private String strDataBaseSqlServer = "POSTOBON_SDI_QAS";
    private Connection conexion;

    private final Logger LOGGER = LoggerFactory.getLogger(co.com.postobon.qa.proyectoapis.utils.ConexionSqlServer1.class);

    public Connection conectarme() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String cadenaConexion = "jdbc:sqlserver://" + strHostSqlServer + ":" + intPortSqlServer + ";databaseName=" + strDataBaseSqlServer + ";user=" + strUserSqlServer + ";password=" + strPasswordSqlServer + ";";
            conexion = DriverManager.getConnection(cadenaConexion);
            LOGGER.info("La conexión a la BD de SQL Server fue exitosa");
        } catch (ClassNotFoundException ex) {
            System.err.println("Error Cargando la Clase " + toString() + "\n" + ex.getMessage());
        } catch (SQLException ex) {
            System.err.println("Error intentando crear la conexion " + toString() + "\n" + ex.getMessage());
            LOGGER.info("Se volverá a realizar la conexión a la BD de SQL Server");
            int intentos = 1;
            while (intentos <= 30) {
                try {
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                } catch (ClassNotFoundException e) {
                    System.err.println("Error Cargando la Clase " + toString() + "\n" + ex.getMessage());
                }
                try {
                    String cadenaConexion = "jdbc:sqlserver://" + strHostSqlServer + ":" + intPortSqlServer + ";databaseName=" + strDataBaseSqlServer + ";user=" + strUserSqlServer + ";password=" + strPasswordSqlServer + ";";
                    conexion = DriverManager.getConnection(cadenaConexion);
                    intentos = 31;
                    LOGGER.info("La conexión a la BD de SQL Server fue exitosa");
                } catch (SQLException e) {
                    LOGGER.info("No se pudo realizar la conexión a la BD de SQL Server");
                }
                intentos++;
            }
        }
        return this.conexion;
    }

    public void close() throws SQLException {
        if (this.conexion != null) {
            this.conexion.close();
        }
    }

    public ResultSet Consultar(String sql) throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conectarme();
        }

        Statement st = conexion.createStatement();
        return st.executeQuery(sql);
    }

}

