package co.com.postobon.qa.proyectoapis.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConsultasBD {

    private static final ConexionSqlServer conexionSqlServer = new ConexionSqlServer();
    private static final ConexionAS400 conexionAS400 = new ConexionAS400();
    private static ResultSet resultadoConsulta;
    private static ResultSetMetaData md;
    private static HashMap<String, Object> fila;
    private static List<HashMap<String, Object>> listaRegistro;
    private static final String AS400 = "AS400";
    private static final String AS400_CENTRAL = "AS400_CENTRAL";
    private static final String SQLSERVER = "SQLSERVER";

    public static List<HashMap<String, Object>> obtenerRegistro(String consulta, String baseDatos) throws SQLException {

        if (baseDatos.equals(AS400)) {
            resultadoConsulta = conexionAS400.Consultar(consulta);
        } else if (baseDatos.equals(AS400_CENTRAL)) {
            resultadoConsulta = conexionAS400.consultarCentral(consulta);
        } else if (baseDatos.equals(SQLSERVER)) {
            resultadoConsulta = conexionSqlServer.Consultar(consulta);
        }

        md = resultadoConsulta.getMetaData();
        int columnasRegistro = md.getColumnCount();
        listaRegistro = new ArrayList<>(columnasRegistro);

        while (resultadoConsulta.next()) {
            fila = new HashMap<>(columnasRegistro);
            for (int i = 1; i <= columnasRegistro; ++i) {
                fila.put(md.getColumnName(i), resultadoConsulta.getObject(i));
            }
            listaRegistro.add(fila);
        }

        if (baseDatos.equals(AS400) || baseDatos.equals(AS400_CENTRAL)) {
            conexionAS400.close();
        } else if (baseDatos.equals(SQLSERVER)) {
            conexionSqlServer.close();
        }

        return listaRegistro;
    }

}
