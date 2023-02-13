package co.com.postobon.qa.proyectoapis.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConsultaDB {
    private Connection conexion;
    ConexionSqlServer conexionSqlServer;


    public ResultSet Consultar( String sql) throws SQLException {
        /*if(conexion == null || conexion.isClosed()){
          conectarme(strMotor);
        }
        */
        //una ves en todas las consultas se cierre cada que se consulte habilito lo de arriba
        //conectarme(strMotor);
        conexion=conexionSqlServer.conectarme();

        Statement st = conexion.createStatement();
        return st.executeQuery(sql);
    }


}
