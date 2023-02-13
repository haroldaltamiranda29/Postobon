package co.com.postobon.qa.proyectoapis.utils;

import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

import static net.serenitybdd.core.Serenity.setSessionVariable;

public class ValidacionesDB {

    static List<String> TrazaLista = new ArrayList<>();
    private static ConsultaDB consultaDB;
    private static Connection conexion;
    static ConexionSqlServer conexionSqlServer = new ConexionSqlServer();
    static ConexionSqlServer1 conexionSqlServer1 = new ConexionSqlServer1();
    static ConexionAS400 conexionAS400 = new ConexionAS400();
    static ConexionAS400Central conexionAS400Central = new ConexionAS400Central();
    static ConexionSAP conexionSAP = new ConexionSAP();
    static ConexionSAPMDQ conexionSAPMDQ = new ConexionSAPMDQ();

    public static String ValidacionDepartamento(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, deptoGuidId = null, deptoId = null, paisId = null, codigoDane = null, nombreDepto = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API Departamento")) {
                Object pathdeptoGuidId = lastResponse.path("departamentos[" + i + "].deptoGuidId");
                //Si esta vacio termina el for
                if (pathdeptoGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                deptoGuidId = pathdeptoGuidId.toString();
                deptoId = lastResponse.path("departamentos[" + i + "].deptoId").toString();
                paisId = lastResponse.path("departamentos[" + i + "].paisId").toString();
                codigoDane = lastResponse.path("departamentos[" + i + "].codigoDane").toString();
                nombreDepto = lastResponse.path("departamentos[" + i + "].nombreDepto").toString();
            } else {
                i = 1000;
                Object pathdeptoGuidId = lastResponse.path("departamentos.deptoGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                deptoGuidId = pathdeptoGuidId.toString();
                deptoId = lastResponse.path("departamentos.deptoId").toString();
                paisId = lastResponse.path("departamentos.paisId").toString();
                codigoDane = lastResponse.path("departamentos.codigoDane").toString();
                nombreDepto = lastResponse.path("departamentos.nombreDepto").toString();
            }

            //VALIDACIÓN ORIGEN
            ResultSet resultadoConsulta = null;
            int fila = conexionSAP.Consultar("T005U", "LAND1", "LAND1 = '" + paisId + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEM02LIP.PVMDEPAR WHERE  DEPART='" + nombreDepto + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.   CODDEP:'" + codigoDane + "' AND DEPART='" + nombreDepto + "'");
                    }
                }

            }
            //VALIDACIÓN DESTINO
            resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_DirDepartamento WHERE  SPaisId='" + paisId + "' AND SDeptoId='" + deptoId + "' AND SDane='" + codigoDane + "' AND SNombre='" + nombreDepto + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.   paisId:" + paisId + " codigoDane:" + codigoDane + " nombreDepto:" + nombreDepto + "");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionMacroCanal(Response lastResponse, String Api) throws SQLException {
        String respuesta, canalGuidId = null, nombre = null, agrupaCompaniaId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API MacroCanal")) {
                Object pathcanalGuidId = lastResponse.path("macroCanal[" + i + "].canalGuidId");
                //Si esta vacio termina el for
                if (pathcanalGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                canalGuidId = pathcanalGuidId.toString();
                nombre = lastResponse.path("macroCanal[" + i + "].nombre").toString();
                agrupaCompaniaId = lastResponse.path("macroCanal[" + i + "].agrupaCompaniaId").toString();

            } else {
                i = 1000;
                Object pathcanalGuidId = lastResponse.path("macroCanal.canalGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                canalGuidId = pathcanalGuidId.toString();
                nombre = lastResponse.path("macroCanal.nombre").toString();
                agrupaCompaniaId = lastResponse.path("macroCanal.agrupaCompaniaId ").toString();

            }

            //VALIDACIÓN ORIGEN
            ResultSet resultadoConsulta = null;
            int fila = conexionSAP.Consultar("TVV1T", "BEZEI", "BEZEI = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                //VALIDACIÓN ORIGEN
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEM02LIP.POMCANAL WHERE  DESCRI='" + nombre + "' AND COMPIA='" + agrupaCompaniaId + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.  DESCRI='" + nombre + "' AND COMPIA='" + agrupaCompaniaId + "");
                    }
                }
            }
            //VALIDACIÓN DESTINO
            resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_Grupo1MacroCanal WHERE SNombre='" + nombre + "' AND NAgrupaCompaniaId='" + agrupaCompaniaId + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNombre='" + nombre + "' AND SAgrupaCompaniaID='" + agrupaCompaniaId + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionMacroSegmento(Response lastResponse, String Api) throws SQLException {
        String respuesta, nombre = null, segmentoGuidId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API MacroSegmento")) {
                Object pathsegmentoGuidId = lastResponse.path("macroSegmento[" + i + "].segmentoGuidId");
                //Si esta vacio termina el for
                if (pathsegmentoGuidId == null) {
                    break;
                }

                DatosObtenidosApi = DatosObtenidosApi + 1;
                nombre = lastResponse.path("macroSegmento[" + i + "].nombre").toString();
                segmentoGuidId = lastResponse.path("macroSegmento[" + i + "].segmentoGuidId").toString();

            } else {
                i = 1000;
                Object pathsegmentoGuidId = lastResponse.path("macroSegmento.segmentoGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                nombre = lastResponse.path("macroSegmento.nombre").toString();

            }
            //VALIDACIÓN ORIGEN
            int fila = conexionSAP.Consultar("TVV2T", "BEZEI", "BEZEI = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    BEZEI:'" + nombre + "");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_Grupo2MacroSegmento WHERE GSegmentoId='" + segmentoGuidId + "' AND SNombre='" + nombre + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.   BEZEI:'" + nombre + "");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }


    public static String ValidacionGrupoSubtipologia(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, subTipologiaIdGuidId = null, nombre = null, subTipologiaId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API SubTipologia")) {
                Object pathsubTipologiaIdGuidId = lastResponse.path("subTipologias[" + i + "].subTipologiaIdGuidId");
                //Si esta vacio termina el for
                if (pathsubTipologiaIdGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                subTipologiaIdGuidId = pathsubTipologiaIdGuidId.toString();
                nombre = lastResponse.path("subTipologias[" + i + "].nombre").toString();
                subTipologiaId = lastResponse.path("subTipologias[" + i + "].subTipologiaId").toString();

            } else {
                i = 1000;
                Object pathsubTipologiaIdGuidId = lastResponse.path("subTipologias.subTipologiaIdGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                subTipologiaIdGuidId = pathsubTipologiaIdGuidId.toString();
                nombre = lastResponse.path("subTipologias.nombre").toString();
                subTipologiaId = lastResponse.path("subTipologias.subTipologiaId").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T151T", "KTEXT", "KTEXT = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    KTEXT:'" + nombre + "");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_Grupo0SubTipologia WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  sSNombre:'" + nombre + "' AND STipologiaId='" + subTipologiaId + "");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }


    public static String ValidacionBarrio(Response lastResponse, String Api) throws SQLException {
        String respuesta, dirBarriosGuiId, nombre;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 10000; i++) {
            if (Api.equals("API Barrio")) {
                Object pathdirBarriosGuiId = lastResponse.path("barrios[" + i + "].dirBarrioGuidId");
                //Si esta vacio termina el for
                if (pathdirBarriosGuiId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                dirBarriosGuiId = pathdirBarriosGuiId.toString();
                nombre = lastResponse.path("barrios[" + i + "].nombre").toString();

            } else {
                i = 10000;
                Object pathdirBarriosGuiId = lastResponse.path("barrios.dirBarrioGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                dirBarriosGuiId = pathdirBarriosGuiId.toString();
                nombre = lastResponse.path("barrios.nombre").toString();

            }

            //Validacion Origen
            int fila = conexionSAPMDQ.Consultar("ZBPC_TT_BARRIO", "BARRIO", "BARRIO = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    BARRIO = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM  SDI.TBL_DirBarrio WHERE SNombre= '" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNombre='" + nombre + "'");
                }
            }

        }


        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionOperaciones(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, operacionId, nombre, nombreCorto;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API Operaciones")) {
                Object pathoperacionId = lastResponse.path("operaciones[" + i + "].operacionId");
                //Si esta vacio termina el for
                if (pathoperacionId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                operacionId = pathoperacionId.toString();
                nombre = lastResponse.path("operaciones[" + i + "].nombre").toString();
                nombreCorto = lastResponse.path("operaciones[" + i + "].nombrecorto").toString();
            } else {
                i = 1000;
                Object pathoperacionId = lastResponse.path("operaciones.operacionId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                operacionId = pathoperacionId.toString();
                nombre = lastResponse.path("operaciones.nombre").toString();
                nombreCorto = lastResponse.path("operaciones.nombreCorto").toString();
            }


            //VALIDACIÓN ORIGEN
            ResultSet resultadoConsulta = null;
            int fila = conexionSAP.Consultar("ZDSD_MOTIVOSDSD", "BEZEI", "BEZEI = 'Sin Tipologia' ");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEM07LIP.VDMOPERA WHERE NOMBRE='" + nombre + "' AND NOMCOR='" + nombreCorto + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.   NOMBRE:'" + nombre + "' AND NOMCOR='" + nombreCorto + "'");
                    }
                }
            }
            //VALIDACIÓN DESTINO
            resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_OperacionPedido WHERE GOperacionId='" + operacionId + "' AND SNombre='" + nombre + "' AND SNombreCorto='" + nombreCorto + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  GOperacionId:'" + operacionId + "' AND SNombre='" + nombre + "' AND SNombreCorto='" + nombreCorto + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionMotivoPedidos(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, motivoId = null, descripcion = null, nombreCorto = null, tipologiaId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API MotivoPedidos")) {
                Object pathmotivoId = lastResponse.path("motivosPedidos[" + i + "].motivoId");
                //Si esta vacio termina el for
                if (pathmotivoId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                motivoId = pathmotivoId.toString();
                descripcion = lastResponse.path("motivosPedidos[" + i + "].descripcion").toString();
                nombreCorto = lastResponse.path("motivosPedidos[" + i + "].nombreCorto").toString();
                tipologiaId = lastResponse.path("motivosPedidos[" + i + "].tipologiaId").toString();

            } else {
                i = 1000;
                Object pathmotivoId = lastResponse.path("motivosPedidos.motivoId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                motivoId = pathmotivoId.toString();
                descripcion = lastResponse.path("motivosPedidos.descripcion").toString();
                nombreCorto = lastResponse.path("motivosPedidos.nombreCorto").toString();
                tipologiaId = lastResponse.path("motivosPedidos.tipologiaId").toString();
            }
            //VALIDACIÓN ORIGEN
            ResultSet resultadoConsulta = null;
            int fila = conexionSAP.Consultar("TVAUT", "BEZEI", "BEZEI = '" + descripcion + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEM07LIP.VDMMOTRE where NOMBRE = '" + descripcion + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    } else {
                        resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEM07LIP.VDMMOTOB where NOMCOR = '" + nombreCorto + "'");
                        while (resultadoConsulta.next()) {
                            if (resultadoConsulta.getInt(1) > 0) {
                                DatosValidadosOrigen = DatosValidadosOrigen + 1;
                            } else {
                                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                                TrazaLista.add("Registro No Encontrado.   NOMCOR = '" + nombreCorto + "'");
                            }
                        }
                    }
                }
            }
            //VALIDACIÓN DESTINO
            resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_MotivoPedido WHERE SNombreCorto='" + nombreCorto + "' AND SNombre='" + descripcion + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.   SNombreCorto:'" + nombreCorto + "' AND SNombre='" + descripcion + "' AND STipo='" + tipologiaId + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionMunicipio(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, municipioGuidId = null, nombre = null, nombreCorto = null, municipioId = null, deptoId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        int datosCorrectosEquivalencia = 0, datosFallidosEquivalencia = 0, datosObtenidosMetadata = 0;
        String canonicoId = null, as400Id = null, sapId = null;
        boolean pruebaPorId = false;
        ResultSet resultadoConsulta;
        for (int i = 0; i < 1140; i++) {
            if (Api.equals("API Municipios")) {
                Object pathdmunicipioGuidId = lastResponse.path("municipios[" + i + "].municipioGuidId");
                canonicoId = lastResponse.path("metadata[" + i + "].canonicoId");
                //Si esta vacio termina el for
                if (pathdmunicipioGuidId == null && canonicoId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                if (canonicoId != null) {
                    datosObtenidosMetadata = datosObtenidosMetadata + 1;
                }
                municipioGuidId = pathdmunicipioGuidId.toString();
                municipioId = lastResponse.path("municipios[" + i + "].municipioId").toString();
                nombre = lastResponse.path("municipios[" + i + "].nombre").toString();
                nombreCorto = lastResponse.path("municipios[" + i + "].nombreCorto").toString();
                deptoId = lastResponse.path("municipios[" + i + "].deptoId").toString();
                as400Id = lastResponse.path("metadata[" + i + "].as400Id");
                sapId = lastResponse.path("metadata[" + i + "].sapId");
            } else {
                Object pathdmunicipioGuidId = lastResponse.path("municipios.municipioGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                datosObtenidosMetadata = datosObtenidosMetadata + 1;
                municipioGuidId = pathdmunicipioGuidId.toString();
                municipioId = lastResponse.path("municipios.municipioId").toString();
                nombre = lastResponse.path("municipios.nombre").toString();
                nombreCorto = lastResponse.path("municipios.nombreCorto").toString();
                deptoId = lastResponse.path("municipios.deptoId").toString();
                canonicoId = lastResponse.path("metadata.canonicoId");
                as400Id = lastResponse.path("metadata.as400Id");
                sapId = lastResponse.path("metadata.sapId");
                pruebaPorId = true;
                break;
            }
            //VALIDACIÓN ORIGEN
            /*int fila = conexionSAPMDQ.Consultar("ADRCITY", "MC_CITY", "MC_CITY = '" + nombreCorto + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEM02LIP.PVMMPIOS WHERE  MUNICI='" + nombre + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.   CMUNICI='" + nombre + "'");
                    }
                }
            }*/
            //VALIDACIÓN DESTINO
            resultadoConsulta = conexionSqlServer.Consultar("SELECT SNombre FROM SDI.TBL_DirMunicipio WHERE  SMunicipioId='" + municipioId + "' AND SDeptoId='" + deptoId + "' AND SNombreCorto='" + nombreCorto + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getString(1).equals(nombre)) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.   SNombre:'" + nombre + "' AND SDeptoId='" + deptoId + "' AND SNombreCorto='" + nombreCorto + "'");
                }
            }
            conexionSqlServer.close();
            //VALIDACIÓN EQUIVALENCIA
            if (canonicoId != null) {
                resultadoConsulta = conexionSqlServer.Consultar("SELECT SAS400Id, SSApId FROM SDI.TBL_EquivalenciaDetalle WHERE GPKId = (SELECT GDirMunicipioGuidId FROM SDI.TBL_DirMunicipio WHERE GDirMunicipioGuidId = '" + canonicoId + "')");
                while (resultadoConsulta.next()) {
                    if (as400Id == null || sapId == null) {
                        if (as400Id == null) {
                            if (resultadoConsulta.getString(1) == null && resultadoConsulta.getString(2).equals(sapId)) {
                                datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                            } else {
                                datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                            }
                        } else {
                            if (resultadoConsulta.getString(1).equals(as400Id) && resultadoConsulta.getString(2) == null) {
                                datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                            } else {
                                datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                            }
                        }
                    } else if (resultadoConsulta.getString(1).equals(as400Id) && resultadoConsulta.getString(2).equals(sapId)) {
                        datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                    } else {
                        datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                    }
                }
            }
        }
        if (pruebaPorId) {
            //VALIDACIÓN ORIGEN
            /*int fila = conexionSAPMDQ.Consultar("ADRCITY", "MC_CITY", "MC_CITY = '" + nombreCorto + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEM02LIP.PVMMPIOS WHERE MUNICI='" + nombre + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.   MUNICI:'" + nombre + "'");
                    }
                }
            }*/
            //VALIDACIÓN DESTINO ID
            resultadoConsulta = conexionSqlServer.Consultar("SELECT SNombre FROM SDI.TBL_DirMunicipio WHERE  SMunicipioId='" + municipioId + "' AND SDeptoId='" + deptoId + "' AND SNombreCorto='" + nombreCorto + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getString(1).equals(nombre)) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.   SNombre='" + nombre + "'");
                }
            }
            conexionSqlServer.close();
            //VALIDACION EQUIVALENCIA ID
            resultadoConsulta = conexionSqlServer.Consultar("SELECT SAS400Id, SSApId FROM SDI.TBL_EquivalenciaDetalle WHERE GPKId = (SELECT GDirMunicipioGuidId FROM SDI.TBL_DirMunicipio WHERE GDirMunicipioGuidId = '" + canonicoId + "')");
            while (resultadoConsulta.next()) {
                if (as400Id == null || sapId == null) {
                    if (as400Id == null) {
                        if (resultadoConsulta.getString(1) == null && resultadoConsulta.getString(2).equals(sapId)) {
                            datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                        } else {
                            datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                        }
                    } else {
                        if (resultadoConsulta.getString(1).equals(as400Id) && resultadoConsulta.getString(2) == null) {
                            datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                        } else {
                            datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                        }
                    }
                } else if (resultadoConsulta.getString(1).equals(as400Id) && resultadoConsulta.getString(2).equals(sapId)) {
                    datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                } else {
                    datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                }
            }
        }


        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Metadata: " + datosObtenidosMetadata);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos Validados Equivalencia: " + datosCorrectosEquivalencia);
        System.out.println("Datos Fallidos Equivalencia: " + datosFallidosEquivalencia);
        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Obtenidos Metadata. " + datosObtenidosMetadata);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Metadata. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Metadata. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionAS400.close();
        conexionSqlServer.close();
        return respuesta;
    }


    /*
        public static String ValidacionRutas(Response lastResponse, String Api) throws SQLException {
            String respuesta = null, rutaID = null, centro = null, centroId;
            int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
            for (int i = 0; i < 1000; i++) {
                if (Api.equals("API Rutas")) {
                    Object pathdrutaID = lastResponse.path("rutas[" + i + "].rutaId");
                    //Si esta vacio termina el for
                    if (pathdrutaID == null) {
                        break;
                    }
                    DatosObtenidosApi = DatosObtenidosApi + 1;
                    rutaID = pathdrutaID.toString();
                    centro = lastResponse.path("rutas[" + i + "].centro").toString();
                    centroId = lastResponse.path("rutas[" + i + "].centroId").toString();
                } else {
                    i = 1000;
                    Object pathdrutaID = lastResponse.path("rutas.rutaID");
                    DatosObtenidosApi = DatosObtenidosApi + 1;
                    rutaID = pathdrutaID.toString();
                    centro = lastResponse.path("rutas.centro").toString();
                    centroId = lastResponse.path("rutas.centroId").toString();

                }

                //se agrego jar as400 en lib y se mdodifico el build.gradle, cargando el jar y diciendole en que carpeta
                //VALIDACIÓN ORIGEN
                ResultSet resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEM02LIP.PVMDEPAR WHERE CODDEP='" + codigoDane + "' AND DEPART='" + nombreDepto + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.  deptoGuidId:" + deptoGuidId + " deptoId:" + deptoId + " paisId:" + paisId + " codigoDane:" + codigoDane + " nombreDepto:" + nombreDepto + "");
                    }
                }


                //VALIDACIÓN DESTINO
                resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_DirDepartamento WHERE GDirDepartamentoGuidId='" + deptoGuidId + "' AND SPaisId='" + paisId + "' AND SDeptoId='" + deptoId + "' AND SDane='" + codigoDane + "' AND SNombre='" + nombreDepto + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosDestino = DatosValidadosDestino + 1;
                    } else {
                        DatosFallidosDestino = DatosFallidosDestino + 1;
                        TrazaLista.add("Registro No Encontrado.  deptoGuidId:" + deptoGuidId + " deptoId:" + deptoId + " paisId:" + paisId + " codigoDane:" + codigoDane + " nombreDepto:" + nombreDepto + "");
                    }
                }

            }
            System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
            System.out.println("Datos " + Api + " Validados Origen AS400. " + DatosValidadosOrigen);
            System.out.println("Datos " + Api + " Fallidos Origen AS400. " + DatosFallidosOrigen);
            TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
            TrazaLista.add("Datos " + Api + " Validados Origen AS400. " + DatosValidadosOrigen);
            TrazaLista.add("Datos " + Api + " Fallidos Origen AS400. " + DatosFallidosOrigen);
            conexionAS400.close();

            System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
            System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
            TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
            TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
            if (DatosObtenidosApi == DatosValidadosDestino) {
                respuesta = "Data " + Api + " Validada Exitosamente";
                TrazaLista.add(respuesta);
            } else {
                respuesta = "Validacion de Data " + Api + " Fallida";
                TrazaLista.add(respuesta);
            }
            conexionSqlServer.close();
            return respuesta;
        }
    */
    public static String ValidacionMisionCompra(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, misionCompraGuidId = null, misionCompraId = null, nombre;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API MisionCompra")) {
                Object pathdmisionCompraGuidId = lastResponse.path("misionCompra[" + i + "].misionCompraGuidId");
                //Si esta vacio termina el for
                if (pathdmisionCompraGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                misionCompraGuidId = pathdmisionCompraGuidId.toString();
                misionCompraId = lastResponse.path("misionCompra[" + i + "].misionCompraId").toString();
                nombre = lastResponse.path("misionCompra[" + i + "].nombre").toString();

            } else {
                i = 1000;
                Object pathdmisionCompraGuidId = lastResponse.path("misionCompra.deptoGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                misionCompraGuidId = pathdmisionCompraGuidId.toString();
                misionCompraId = lastResponse.path("misionCompra.misionCompraId").toString();
                nombre = lastResponse.path("misionCompra.nombre").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("TVV5T", "BEZEI", "BEZEI = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    BEZEI:'" + nombre + "");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_Grupo5MisionCompra WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNombre:'" + nombre + "");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionSector(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, sectorId = null, sectorGuidId = null, nombre;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API Sector")) {
                Object pathsectorGuidId = lastResponse.path("sectores[" + i + "].sectorGuidId");
                //Si esta vacio termina el for
                if (pathsectorGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                sectorGuidId = pathsectorGuidId.toString();
                sectorId = lastResponse.path("sectores[" + i + "].sectorId").toString();
                nombre = lastResponse.path("sectores[" + i + "].nombre").toString();

            } else {
                i = 1000;
                Object pathsectorGuidId = lastResponse.path("sectores.sectorGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                sectorGuidId = pathsectorGuidId.toString();
                sectorId = lastResponse.path("misionCompra.misionCompraId").toString();
                nombre = lastResponse.path("sectores.nombre").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("TSPA", "SPART", "SPART = '" + sectorId + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    SPART = '" + sectorId + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM  SDI.TBL_Sector WHERE SSectorId='" + sectorId + "' AND SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  GSSectorId='" + sectorId + "' AND SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionPartidaAbierta(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, partidaAbiertaGuidId = null, docId = null, clienteId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API PartidasAbiertas")) {
                Object pathpartidaAbiertaGuidId = lastResponse.path("partidasAbiertas[" + i + "].partidaAbiertaGuidId");
                //Si esta vacio termina el for
                if (pathpartidaAbiertaGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                partidaAbiertaGuidId = pathpartidaAbiertaGuidId.toString();
                docId = lastResponse.path("partidasAbiertas[" + i + "].docId").toString();
                clienteId = lastResponse.path("partidasAbiertas[" + i + "].clienteId").toString();

            } else {
                i = 1000;
                Object pathpartidaAbiertaGuidId = lastResponse.path("partidasAbiertas.guidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                partidaAbiertaGuidId = pathpartidaAbiertaGuidId.toString();
                docId = lastResponse.path("partidasAbiertas.docId").toString();
                clienteId = lastResponse.path("partidasAbiertas.clienteId").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("/DSD/ME_OPIM_HD", "DOC_NO", "DOC_NO = '" + docId + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    DOC_NO = '" + docId + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_PartidaAbierta WHERE SDocId='" + docId + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SDocId='" + docId + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionZonaVenta(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, guidId = null, nombre = null, id = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API Ventas")) {
                Object pathguidId = lastResponse.path("zonasVenta[" + i + "].guidId");
                //Si esta vacio termina el for
                if (pathguidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                guidId = pathguidId.toString();
                nombre = lastResponse.path("zonasVenta[" + i + "].nombre").toString();
                id = lastResponse.path("zonasVenta[" + i + "].id").toString();

            } else {
                i = 1000;
                Object pathguidId = lastResponse.path("zonasVenta.guidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                guidId = pathguidId.toString();
                nombre = lastResponse.path("zonasVenta.nombre").toString();
                id = lastResponse.path("zonasVenta.id").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T171T", "BZIRK", "BZIRK = '" + id + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    BZIRK = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_ZonaVenta WHERE  SZonaId='" + id + "' AND SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SZonaId:'" + id + "' AND SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionModeloAtencion(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, modeloAtencionGuidId = null, nombre = null, modeloAtencionId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API ModeloAtencion")) {
                Object pathmodeloAtencionGuidId = lastResponse.path("modeloAtencion[" + i + "].modeloAtencionGuidId");
                //Si esta vacio termina el for
                if (pathmodeloAtencionGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                modeloAtencionGuidId = pathmodeloAtencionGuidId.toString();
                nombre = lastResponse.path("modeloAtencion[" + i + "].nombre").toString();
                modeloAtencionId = lastResponse.path("modeloAtencion[" + i + "].modeloAtencionId").toString();

            } else {
                i = 1000;
                Object pathmodeloAtencionGuidId = lastResponse.path("modeloAtencion.modeloAtencionGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                modeloAtencionGuidId = pathmodeloAtencionGuidId.toString();
                nombre = lastResponse.path("modeloAtencion.nombre").toString();
                modeloAtencionId = lastResponse.path("modeloAtencion.modeloAtencionId").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("TVV4T", "BEZEI", "BEZEI = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    BEZEI = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_Grupo4ModeloAtencion WHERE  SModeloAtencionId='" + modeloAtencionId + "' AND SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SModeloAtencionId='" + modeloAtencionId + "' AND SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionPais(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, paisGuidId = null, nombre = null, paisId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API Pais")) {
                Object pathpaisGuidId = lastResponse.path("paises[" + i + "].paisGuidId");
                //Si esta vacio termina el for
                if (pathpaisGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                paisGuidId = pathpaisGuidId.toString();
                nombre = lastResponse.path("paises[" + i + "].nombre").toString();
                paisId = lastResponse.path("paises[" + i + "].paisId").toString();

            } else {
                i = 1000;
                Object pathpaisGuidId = lastResponse.path("paises.paisGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                paisGuidId = pathpaisGuidId.toString();
                nombre = lastResponse.path("paises.nombre").toString();
                paisId = lastResponse.path("paises.paisId").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T005T", "LANDX", "LANDX = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    LANDX = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_DirPais WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionCodicionPago(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, condicionPagoGuidId = null, nombre = null, condicionPagoId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API CondicionPago")) {
                Object pathcondicionPagoGuidId = lastResponse.path("condicionesPago[" + i + "].condicionPagoGuidId");
                //Si esta vacio termina el for
                if (pathcondicionPagoGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                condicionPagoGuidId = pathcondicionPagoGuidId.toString();
                nombre = lastResponse.path("condicionesPago[" + i + "].nombre").toString();
                condicionPagoId = lastResponse.path("condicionesPago[" + i + "].condicionPagoId").toString();

            } else {
                i = 1000;
                Object pathcondicionPagoGuidId = lastResponse.path("condicionesPago.condicionPagoGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                condicionPagoGuidId = pathcondicionPagoGuidId.toString();
                nombre = lastResponse.path("condicionesPago.nombre").toString();
                condicionPagoId = lastResponse.path("condicionesPago.condicionPagoId").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T052U", "ZTERM", "ZTERM = '" + condicionPagoId + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    ZTERM = '" + condicionPagoId + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_CondicionPago WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionOrganizacionVenta(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, orgVentaGuidId = null, nombre = null, orgVentaId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API OrganizacionesVenta")) {
                Object pathorgVentaGuidId = lastResponse.path("organizacionesVenta[" + i + "].orgVentaGuidId");
                //Si esta vacio termina el for
                if (pathorgVentaGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                orgVentaGuidId = pathorgVentaGuidId.toString();
                nombre = lastResponse.path("organizacionesVenta[" + i + "].nombre").toString();
                orgVentaId = lastResponse.path("organizacionesVenta[" + i + "].orgVentaId").toString();

            } else {
                i = 1000;
                Object pathcondicionPagoGuidId = lastResponse.path("condicionesPago.condicionPagoGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                orgVentaGuidId = pathcondicionPagoGuidId.toString();
                nombre = lastResponse.path("condicionesPago.nombre").toString();
                orgVentaId = lastResponse.path("condicionesPago.orgVentaId").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("TVKOT", "VKORG", "VKORG = '" + orgVentaId + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    VKORG = '" + orgVentaId + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_OrganizacionVenta WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionUnidadMedida(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, unidadGuidId = null, nombre = null, unidadId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API UnidadMedida")) {
                Object pathunidadGuidId = lastResponse.path("unidadesMedida[" + i + "].unidadGuidId");
                //Si esta vacio termina el for
                if (pathunidadGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                unidadGuidId = pathunidadGuidId.toString();
                nombre = lastResponse.path("unidadesMedida[" + i + "].nombre").toString();
                unidadId = lastResponse.path("unidadesMedida[" + i + "].unidadId").toString();

            } else {
                i = 1000;
                Object pathsubTipologiaIdGuidId = lastResponse.path("unidadesMedida.unidadGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                unidadGuidId = pathsubTipologiaIdGuidId.toString();
                nombre = lastResponse.path("unidadesMedida.nombre").toString();
                unidadId = lastResponse.path("unidadesMedida.unidadId").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T006A", "MSEHL", "MSEHL = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    MSEHL = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_UnidadMedida WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionMasivaEsquemaCalculo(Response lastResponse, String Api) throws SQLException {
        String respuesta = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        if (Api.equals("API EsquemaCalculo")) {
            ResultSet resultadoConsultaBD = conexionSqlServer.Consultar("Select SEsquemaId from SDI.TBL_EsquemaCalculo");
            while (resultadoConsultaBD.next()) {
                DatosObtenidosApi = DatosObtenidosApi + 1;
                //Validacion Origen
                int fila = conexionSAP.Consultar("ZENVESC", "KALSM", "KALSM = '" + resultadoConsultaBD.getString("SEsquemaId") + "'");
                if (fila > 0) {
                    DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosOrigen = DatosFallidosOrigen + 1;
                    DatosFallidosDestino = DatosFallidosOrigen + 1;
                    TrazaLista.add("Registro No Encontrado.    KALSM = '" + resultadoConsultaBD.getString("SEsquemaId") + "'");
                }
            }
        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionDireccionesCardinalidad(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, cardinalidadGuidId = null, nombre;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 10; i++) {
            if (Api.equals("API DireccionCardinalidad")) {
                Object pathcardinalidadGuidId = lastResponse.path("cardinalidades[" + i + "].cardinalidadGuidId");
                //Si esta vacio termina el for
                if (pathcardinalidadGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                cardinalidadGuidId = pathcardinalidadGuidId.toString();
                nombre = lastResponse.path("cardinalidades[" + i + "].nombre").toString();

            } else {
                i = 10;
                Object pathcardinalidadGuidId = lastResponse.path("cardinalidades.cardinalidadGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                cardinalidadGuidId = pathcardinalidadGuidId.toString();
                nombre = lastResponse.path("cardinalidades.nombre").toString();


            }
            //Validacion Origen
            int fila = conexionSAPMDQ.Consultar("ZBVTT004_CARDINA", "CARDINALIDAD", "CARDINALIDAD = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    CARDINALIDAD = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_DirCardinalidad WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  sSNombre:'" + nombre + "' AND SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionDireccionesComplemento(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, complementoGuidId = null, nombre;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 100; i++) {
            if (Api.equals("API DireccionComplemento")) {
                Object pathcomplementoGuidId = lastResponse.path("complementos[" + i + "].complementoGuidId");
                //Si esta vacio termina el for
                if (pathcomplementoGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                complementoGuidId = pathcomplementoGuidId.toString();
                nombre = lastResponse.path("complementos[" + i + "].nombre").toString();

            } else {
                i = 100;
                Object pathcomplementoGuidId = lastResponse.path("complementos.nombre");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                complementoGuidId = pathcomplementoGuidId.toString();
                nombre = lastResponse.path("complementos.nombre").toString();
            }
            //Validacion Origen
            int fila = conexionSAPMDQ.Consultar("ZBVTT005_COMPLEM", "COMPLEMENTO", "COMPLEMENTO = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    COMPLEMENTO = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_DirComplemento WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  sSNombre:'" + nombre + "' AND SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionDireccionesNomenclatura(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, nomenclaturaGuidId = null, nombre, id;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 200; i++) {
            if (Api.equals("API DireccionNomenclatura")) {
                Object pathnomenclaturaGuidId = lastResponse.path("nomenclaturas[" + i + "].nomenclaturaGuidId");
                //Si esta vacio termina el for
                if (pathnomenclaturaGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                nomenclaturaGuidId = pathnomenclaturaGuidId.toString();
                nombre = lastResponse.path("nomenclaturas[" + i + "].nombre").toString();
                id = lastResponse.path("nomenclaturas[" + i + "].nomenclaturaId").toString();

            } else {
                i = 200;
                Object pathnomenclaturaGuidId = lastResponse.path("nomenclaturas.nombre");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                nomenclaturaGuidId = pathnomenclaturaGuidId.toString();
                nombre = lastResponse.path("nomenclaturas.nombre").toString();
                id = lastResponse.path("nomenclaturas[" + i + "].nomenclaturaId").toString();
            }
            //Validacion Origen
            int fila = conexionSAPMDQ.Consultar("ZBVTT006_NOMENCL", "NOMENCLATURA", "NOMENCLATURA = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    NOMENCLATURA = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_DirNomenclatura WHERE SNomenclaturaId='" + id + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNomenclaturaId='" + id + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionDireccionesVia(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, viaGuidId = null, nombre;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 20; i++) {
            if (Api.equals("API DireccionVia")) {
                Object pathviaGuidId = lastResponse.path("vias[" + i + "].viaGuidId");
                //Si esta vacio termina el for
                if (pathviaGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                viaGuidId = pathviaGuidId.toString();
                nombre = lastResponse.path("vias[" + i + "].nombre").toString();

            } else {
                i = 20;
                Object pathviaGuidId = lastResponse.path("vias.nombre");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                viaGuidId = pathviaGuidId.toString();
                nombre = lastResponse.path("vias.nombre").toString();


            }
            //Validacion Origen
            int fila = conexionSAPMDQ.Consultar("ZBVTT007_VIA", "VIA", "VIA = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    VIA = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_DirVia WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  sSNombre:'" + nombre + "' AND SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }


    public static String ValidacionRepresentanteVenta(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, vendedorId, nombre, nombre2;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 200; i++) {
            if (Api.equals("API RepresentanteVenta")) {
                Object pathVendedorId = lastResponse.path("representanteVentas[" + i + "].vendedorId");
                //Si esta vacio termina el for
                if (pathVendedorId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                vendedorId = pathVendedorId.toString();
                nombre = lastResponse.path("representanteVentas[" + i + "].nombre.nombre1").toString();

            } else {
                i = 200;
                Object pathviaGuidId = lastResponse.path("representanteVenta.vendedorId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                nombre = lastResponse.path("representanteVenta.nombre.nombre1").toString();
                nombre2 = lastResponse.path("representanteVenta.nombre.nombre2").toString();
            }

            //VALIDACIÓN ORIGEN
            ResultSet resultadoConsulta = null;
            int fila = conexionSAP.Consultar("/DSD/ME_CUST_HD", "NAME3", "NAME3 = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEM07LIP.VDMVENDE WHERE  NOMBRE='" + nombre + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.   NOMBRE='" + nombre + "'");
                    }
                }

            }
            //VALIDACIÓN DESTINO
            resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_RepresentanteVenta WHERE  SNombre='" + nombre + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.   SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionSociedades(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, sociedadId = null, nombre = null, codigoSociedad = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API Sociedad")) {
                Object pathsociedadId = lastResponse.path("sociedad[" + i + "].sociedadId");
                //Si esta vacio termina el for
                if (pathsociedadId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                sociedadId = pathsociedadId.toString();
                nombre = lastResponse.path("sociedad[" + i + "].nombre").toString();
                codigoSociedad = lastResponse.path("sociedad[" + i + "].codigoSociedad").toString();


            } else {
                i = 1000;
                Object pathsociedadId = lastResponse.path("sociedad.sociedadId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                sociedadId = pathsociedadId.toString();
                nombre = lastResponse.path("sociedad.sociedadId").toString();
                codigoSociedad = lastResponse.path("sociedad[" + i + "].codigoSociedad").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T001", "BUKRS", "BUKRS = '" + codigoSociedad + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    BUKRS = '" + codigoSociedad + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM  SDI.TBL_Sociedad WHERE SNombre='" + nombre + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNombre='" + nombre + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionCentroSociedad(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, sociedadId = null, centroSociedadId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API CentroSociedad")) {
                Object pathcentroSociedadId = lastResponse.path("centroSociedad[" + i + "].centroSociedadId");
                //Si esta vacio termina el for
                if (pathcentroSociedadId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                centroSociedadId = pathcentroSociedadId.toString();
                sociedadId = lastResponse.path("centroSociedad[" + i + "].sociedadId").toString();


            } else {
                i = 1000;
                Object pathcentroSociedadId = lastResponse.path("sociedad.sociedadId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                centroSociedadId = pathcentroSociedadId.toString();
                sociedadId = lastResponse.path("centroSociedad.sociedadId").toString();


            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T001K", "BUKRS", "BUKRS = '" + sociedadId + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    BUKRS = '" + sociedadId + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM  SDI.TBL_SociedadCentro WHERE GSociedadCentroGuidId='" + centroSociedadId + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SSociedadId='" + sociedadId + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionclaseCondicion(Response lastResponse, String Api) throws SQLException {
        String respuesta, claseCondicionGuidId, nombre = null, claseCondicionId = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API ClaseCondicion")) {
                Object pathclaseCondicionGuidId = lastResponse.path("claseCondiciones[" + i + "].claseCondicionGuidId");
                //Si esta vacio termina el for
                if (pathclaseCondicionGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                claseCondicionGuidId = pathclaseCondicionGuidId.toString();
                nombre = lastResponse.path("claseCondiciones[" + i + "].nombre").toString();
                claseCondicionId = lastResponse.path("claseCondiciones[" + i + "].claseCondicionId").toString();

            } else {
                i = 1000;
                Object pathclaseCondicionGuidId = lastResponse.path("claseCondiciones.claseCondicionGuidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                claseCondicionGuidId = pathclaseCondicionGuidId.toString();
                nombre = lastResponse.path("claseCondiciones.nombre").toString();
                claseCondicionId = lastResponse.path("claseCondiciones.claseCondicionId").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T685T", "KSCHL", "KSCHL = '" + claseCondicionId + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    KSCHL = '" + claseCondicionId + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_ClaseCondicion WHERE SNombre ='" + nombre + "' ");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SNombre ='" + nombre + "' ");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }


    public static String ValidacionSecuenciaAcceso(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, secuenciaAccesoGuidId = null, tablaCondicion = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API SecuenciaAcceso")) {
                Object pathsecuenciaAccesoGuidId = lastResponse.path("secuenciaAcceso[" + i + "].secuenciaAccesoGuidId");
                //Si esta vacio termina el for
                if (pathsecuenciaAccesoGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                secuenciaAccesoGuidId = pathsecuenciaAccesoGuidId.toString();
                tablaCondicion = lastResponse.path("secuenciaAcceso[" + i + "].tablaCondicion").toString();


            } else {
                i = 1000;
                Object pathsecuenciaAccesoGuidId = lastResponse.path("secuenciaAcceso.tablaCondicion");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                secuenciaAccesoGuidId = pathsecuenciaAccesoGuidId.toString();
                tablaCondicion = lastResponse.path("secuenciaAcceso[" + i + "].tablaCondicion").toString();


            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T682I", "KOTABNR", "KOTABNR = '" + tablaCondicion + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    KOTABNR = '" + tablaCondicion + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM  SDI.TBL_SecuenciaAcceso WHERE STablaCondicion='" + tablaCondicion + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  STablaCondicion='" + tablaCondicion + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionSecuenciaAccesoCampo(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, secuenciaAccesoCampoGuidId = null, utilizacion = null, aplicacion;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API SecuenciaAccesoCampo")) {
                Object pathsecuenciaAccesoCampoGuidId = lastResponse.path("secuenciaAccesoCampo[" + i + "].secuenciaAccesoCampoGuidId");
                //Si esta vacio termina el for
                if (pathsecuenciaAccesoCampoGuidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                secuenciaAccesoCampoGuidId = pathsecuenciaAccesoCampoGuidId.toString();
                utilizacion = lastResponse.path("secuenciaAccesoCampo[" + i + "].secuenciaAccesoCampoGuidId").toString();
                aplicacion = lastResponse.path("secuenciaAccesoCampo[" + i + "].aplicacion").toString();

            } else {
                i = 1000;
                Object pathsecuenciaAccesoCampoGuidId = lastResponse.path("secuenciaAcceso.tablaCondicion");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                secuenciaAccesoCampoGuidId = pathsecuenciaAccesoCampoGuidId.toString();
                utilizacion = lastResponse.path("secuenciaAccesoCampo[" + i + "].secuenciaAccesoCampoGuidId").toString();
                aplicacion = lastResponse.path("secuenciaAccesoCampo[" + i + "].aplicacion").toString();

            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T682Z", "KAPPL", "KAPPL = '" + aplicacion + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    KAPPL = '" + aplicacion + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM  SDI.TBL_SecuenciaAccesoCampo WHERE SAplicacion='" + aplicacion + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  SAplicacion='" + aplicacion + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionGrupoArticulos(Response lastResponse, String Api) throws SQLException {
        String respuesta, nombre = null, grupoArticuloId = null, claveIdioma;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API GrupoArticulo")) {
                Object pathgrupoArticuloId = lastResponse.path("grupoArticulos[" + i + "].grupoArticuloId");
                //Si esta vacio termina el for
                if (pathgrupoArticuloId == null) {
                    break;
                }

                DatosObtenidosApi = DatosObtenidosApi + 1;
                nombre = lastResponse.path("grupoArticulos[" + i + "].nombre").toString();
                grupoArticuloId = lastResponse.path("grupoArticulos[" + i + "].grupoArticuloId").toString();
                claveIdioma = lastResponse.path("grupoArticulos.claveIdioma").toString();

            } else {
                i = 1000;
                Object pathgrupoArticuloId = lastResponse.path("grupoArticulos.grupoArticuloId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                grupoArticuloId = pathgrupoArticuloId.toString();
                nombre = lastResponse.path("grupoArticulos.nombre").toString();
                grupoArticuloId = lastResponse.path("grupoArticulos[" + i + "].grupoArticuloId").toString();
                claveIdioma = lastResponse.path("grupoArticulos.claveIdioma").toString();
            }

            ResultSet resultadoConsulta = null;
            int fila = conexionSAP.Consultar("T023T", "SPRAS", "SPRAS = '" + claveIdioma + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEMXXWM.TPGRUPEN WHERE  DESGRU='" + nombre + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.  DESGRU='" + nombre + "'");
                    }
                }
            }


            //VALIDACIÓN DESTINO
            resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_GrupoArticulo WHERE GGrupoArticuloGuidId='" + grupoArticuloId + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.   GGrupoArticuloId='" + grupoArticuloId + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();


        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionJerarquiaMateriales(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, jerarquiaProductoId = null, nombre = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API JerarquiaMaterial")) {
                Object pathjerarquiaProductoId = lastResponse.path("jerarquia[" + i + "].jerarquiaProductoId");
                //Si esta vacio termina el for
                if (pathjerarquiaProductoId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                jerarquiaProductoId = pathjerarquiaProductoId.toString();
                nombre = lastResponse.path("jerarquia[" + i + "].nombre").toString();


            } else {
                i = 1000;
                Object pathjerarquiaProductoId = lastResponse.path("jerarquia.jerarquiaProductoId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                jerarquiaProductoId = pathjerarquiaProductoId.toString();
                nombre = lastResponse.path("jerarquia.nombre").toString();
            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("T179T", "VTEXT", "VTEXT = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    VTEXT = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_Jerarquia5Presentacion WHERE GJerarquia5PresentacionId='" + jerarquiaProductoId + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  GJerarquia5PresentacionId='" + jerarquiaProductoId + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionGrupoVendedores(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, grupoVendedorId = null, nombre = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 10; i++) {
            if (Api.equals("API GrupoVendedores")) {
                Object pathgrupoVendedorId = lastResponse.path("grupoVendedores[" + i + "].grupoVendedorId");
                //Si esta vacio termina el for
                if (pathgrupoVendedorId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                grupoVendedorId = pathgrupoVendedorId.toString();
                nombre = lastResponse.path("grupoVendedores[" + i + "].nombre").toString();


            } else {
                i = 1000;
                Object pathgrupoVendedorId = lastResponse.path("grupoVendedores.grupoVendedorId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                grupoVendedorId = pathgrupoVendedorId.toString();
                nombre = lastResponse.path("grupoVendedores.nombre").toString();
            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("TVGRT", "BEZEI", "BEZEI = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    BEZEI = '" + nombre + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_GrupoVendedor WHERE GGrupoVendedorGuidId='" + grupoVendedorId + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  GGrupoVendedorGuidId='" + grupoVendedorId + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionJerarquiaSupervisor(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, guidId = null, personalId = null, cargo;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        for (int i = 0; i < 200; i++) {
            if (Api.equals("API JerarquiaRepresentante")) {
                Object pathguidId = lastResponse.path("jerarquiasRepresentanteSupervisor[" + i + "].guidId");
                //Si esta vacio termina el for
                if (pathguidId == null) {
                    break;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                guidId = pathguidId.toString();
                personalId = lastResponse.path("jerarquiasRepresentanteSupervisor[" + i + "].personalId").toString();
                cargo = lastResponse.path("jerarquiasRepresentanteSupervisor[" + i + "].cargo").toString();

            } else {
                i = 1000;
                Object pathguidId = lastResponse.path("jerarquiasRepresentanteSupervisor.guidId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                guidId = pathguidId.toString();
                personalId = lastResponse.path("jerarquiasRepresentanteSupervisor.personalId").toString();
                cargo = lastResponse.path("jerarquiasRepresentanteSupervisor[" + i + "].cargo").toString();
            }
            //Validacion Origen
            int fila = conexionSAP.Consultar("ZBPTA011", "STEXT", "STEXT = '" + cargo + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                TrazaLista.add("Registro No Encontrado.    STEXT = '" + cargo + "'");
            }
            //VALIDACIÓN DESTINO
            ResultSet resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_JerarquiaSupervisor WHERE GJeraSupervisorGuidId='" + guidId + "'");

            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  GJeraSupervisorGuidId='" + guidId + "'");
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionMasivaCalendario(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, texto = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        int contador = 0;
        if (Api.equals("API Calendario")) {
            ResultSet resultadoConsultaBD = conexionSqlServer.Consultar("Select SEstado from SDI.TBL_Calendario");
            while (resultadoConsultaBD.next()) {
                DatosObtenidosApi = DatosObtenidosApi + 1;
                //VALIDACIÓN ORIGEN
                int fila = conexionSAP.Consultar("ZBVT219", "APROB", "APROB = '" + resultadoConsultaBD.getString("SEstado") + "'");
                if (fila > 0) {
                    DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                    contador++;
                    System.out.println("Registros: " + contador);
                } else {
                    DatosFallidosOrigen = DatosFallidosOrigen + 1;
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.  APROB='" + resultadoConsultaBD.getString("SEstado") + "'");
                }
            }
            ResultSet resultadoConsulta = conexionSqlServer1.Consultar("select  top 1  NRegistrosAfectados from S_SDI.TBL_LogMasivas where SEntidad = 'Calendario'  and SObjetoAfectado = 'TBL_Calendario' ORDER BY DFechaInicio desc");
            while (resultadoConsulta.next()) {
                texto = resultadoConsulta.getString("NRegistrosAfectados");
            }
        }

        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Datos Enviados Celuweb  " + texto);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionMasivaContrato(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, texto = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        if (Api.equals("API Contrato")) {
            ResultSet resultadoConsultaBD = conexionSqlServer.Consultar("Select SClaseDocId from SDI.TBL_Contrato");
            while (resultadoConsultaBD.next()) {
                DatosObtenidosApi = DatosObtenidosApi + 1;
                //VALIDACIÓN ORIGEN
                int fila = conexionSAP.Consultar("ZBDSDT008", "AUART", "AUART = '" + resultadoConsultaBD.getString("SClaseDocId") + "'");
                if (fila > 0) {
                    DatosValidadosOrigen = DatosValidadosOrigen + 1;
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosOrigen = DatosFallidosOrigen + 1;
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    System.out.println("Registro no encontrado. AUART: " + resultadoConsultaBD.getString("SClaseDocId"));
                    TrazaLista.add("Registro No Encontrado.  AUART='" + resultadoConsultaBD.getString("SClaseDocId") + "'");
                }
            }
            ResultSet resultadoConsulta = conexionSqlServer1.Consultar("select  top 1  NRegistrosAfectados from S_SDI.TBL_LogMasivas where SEntidad = 'Contrato'  and SObjetoAfectado = 'TBL_Contrato' ORDER BY DFechaInicio desc");
            while (resultadoConsulta.next()) {
                texto = resultadoConsulta.getString("NRegistrosAfectados");
            }
        }

        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Datos Enviados Celuweb  " + texto);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
            Utils.Imprimir(TrazaLista);
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionMasivaPresupuestoEnvase(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, texto = null;
        int DatosObtenidosApi = 0, Contador = 0, contador = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        List<HashMap<String, Object>> listaRegistro;
        int consultaSAP;
        String SRuta;
        String consultaSQL = "select SRuta from SDI.TBL_PresupuestoEnvase";
        listaRegistro = ConsultasBD.obtenerRegistro(consultaSQL, "SQLSERVER");
        for (int i = 0; i < listaRegistro.size(); i++) {
            SRuta = listaRegistro.get(i).get("SRuta").toString().trim();
            ResultSet consultaAS400 = null;
            consultaSAP = conexionSAP.Consultar("S600", "ROUTE", "ROUTE = '"
                    + listaRegistro.get(i).get("SRuta") + "'");
            DatosObtenidosApi = DatosObtenidosApi + 1;
            if (consultaSAP > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
                DatosValidadosDestino = DatosValidadosDestino + 1;
                Contador++;
                System.out.println("Registros del sap: " + Contador);
            } else {
                consultaAS400 = conexionAS400.Consultar("select count(*) FROM VEM02LIP.VMOVPRESFE WHERE  CODZON='"
                        + listaRegistro.get(i).get("SRuta") + "'");
                while (consultaAS400.next()) {
                    if (consultaAS400.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                        contador++;
                        System.out.println("Registros del as400: " + contador);
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.  CODZON='"
                                + listaRegistro.get(i).get("SRuta") + "'");
                    }
                }
                conexionAS400.close();
            }
            conexionSqlServer.close();
        }
        ResultSet resultadoConsulta = conexionSqlServer1.Consultar("select  top 1  NRegistrosAfectados from S_SDI.TBL_LogMasivas where SEntidad = 'PresupuestoEnvase'  and SObjetoAfectado = 'TBL_PresupuestoEnvase' ORDER BY DFechaInicio desc");
        while (resultadoConsulta.next()) {
            texto = resultadoConsulta.getString("NRegistrosAfectados");
        }

        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Datos Enviados Celuweb  " + texto);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
            Utils.Imprimir(TrazaLista);
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        return respuesta;
    }

    public static String ValidacionMasivaListadosExclusion(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, texto = null;
        int DatosObtenidosApi = 0, Contador = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0, contador = 0;
        List<HashMap<String, Object>> listaRegistro;
        int consultaSAP;
        String SCondicionClase;
        String consultaSQL = "select SCondicionClase from SDI.TBL_ListadoExclusion";
        listaRegistro = ConsultasBD.obtenerRegistro(consultaSQL, "SQLSERVER");
        for (int i = 0; i < listaRegistro.size(); i++) {
            SCondicionClase = listaRegistro.get(i).get("SCondicionClase").toString().trim();
            consultaSAP = conexionSAP.Consultar("/DSD/PE_CNDRDH", "KSCHL", "KSCHL = '"
                    + listaRegistro.get(i).get("SCondicionClase") + "'");
            DatosObtenidosApi = DatosObtenidosApi + 1;
            if (consultaSAP > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
                DatosValidadosDestino = DatosValidadosDestino + 1;
                Contador++;
                System.out.println("Registros: " + Contador);
            } else {
                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                DatosFallidosDestino = DatosFallidosDestino + 1;
                TrazaLista.add("Registro No Encontrado.  KSCHL='" + listaRegistro.get(i).get("SCondicionClase") + "'");
            }
            conexionSqlServer.close();
        }
        ResultSet resultadoConsulta = conexionSqlServer1.Consultar("select top 1 NRegistrosAfectados from S_SDI.TBL_LogMasivas where SEntidad = 'ListadosExclusion' and SObjetoAfectado = 'TBL_ListadoExclusion' ORDER BY DFechaInicio desc");
        while (resultadoConsulta.next()) {
            texto = resultadoConsulta.getString("NRegistrosAfectados");
        }

        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Datos Enviados Celuweb  " + texto);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        conexionAS400.close();

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        return respuesta;
    }

    public static String ValidacionMasivaEquipoVehiculo(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, texto = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0, Contador = 0;
        List<HashMap<String, Object>> listaRegistro;
        int consultaSAP;
        String SEquipoId, SCentroPlaniMantenId, SSerial, SFabricante, SClasificacion;
        String consultaSQL = "select SEquipoId" +
                " from SDI.TBL_Equipo" +
                " where STipoObjDesc = 'Vehiculo'";
        listaRegistro = ConsultasBD.obtenerRegistro(consultaSQL, "SQLSERVER");
        for (int i = 0; i < listaRegistro.size(); i++) {
            SEquipoId = listaRegistro.get(i).get("SEquipoId").toString().trim();
            ResultSet consultaAS400 = null;
            consultaSAP = conexionSAP.Consultar("EQUI" + "", "EQUNR", "EQUNR = '"
                    + listaRegistro.get(i).get("SEquipoId") + "'");
            DatosObtenidosApi = DatosObtenidosApi + 1;
            if (consultaSAP > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
                DatosValidadosDestino = DatosValidadosDestino + 1;
                Contador++;
                System.out.println("Registros: " + Contador);
            } else {
                consultaAS400 = conexionAS400.Consultar("select count(*) FROM VEMXXWM.TPMAIEMHMM WHERE  NUMEQU='"
                        + listaRegistro.get(i).get("SEquipoId") + "'");
                while (consultaAS400.next()) {
                    if (consultaAS400.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                        Contador++;
                        System.out.println("Registros: " + Contador);
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.  NUMEQU='" + listaRegistro.get(i).get("SEquipoId") + "'");
                    }
                }
                conexionAS400.close();
            }
            conexionSqlServer.close();
            ResultSet resultadoConsulta = conexionSqlServer1.Consultar("select top 1 NRegistrosAfectados from S_SDI.TBL_LogMasivas where SEntidad = 'EquipoVehiculo' and SObjetoAfectado = 'TBL_EquipoVehiculo' ORDER BY DFechaInicio desc");
            while (resultadoConsulta.next()) {
                texto = resultadoConsulta.getString("NRegistrosAfectados");
            }
        }

        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Datos Enviados Celuweb  " + texto);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        return respuesta;
    }

    public static String ValidacionMasivaEquipoNevera(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, texto = null;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0, Contador = 0;
        List<HashMap<String, Object>> listaRegistro;
        int consultaSAP;
        String SEquipoId;
        String consultaSQL = "select SEquipoId" +
                " from SDI.TBL_Equipo" +
                " where STipoObjDesc = 'Nevera'";
        listaRegistro = ConsultasBD.obtenerRegistro(consultaSQL, "SQLSERVER");
        for (int i = 0; i < listaRegistro.size(); i++) {
            SEquipoId = listaRegistro.get(i).get("SEquipoId").toString().trim();
            ResultSet consultaAS400 = null;
            consultaSAP = conexionSAP.Consultar("EQUI" + "", "EQUNR", "EQUNR = '"
                    + listaRegistro.get(i).get("SEquipoId") + "'");
            DatosObtenidosApi = DatosObtenidosApi + 1;
            if (consultaSAP > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
                DatosValidadosDestino = DatosValidadosDestino + 1;
                Contador++;
                System.out.println("Registros: " + Contador);
            } else {
                consultaAS400 = conexionAS400.Consultar("select count(*) FROM VEMXXWM.TPMAIEMHMM WHERE  NUMEQU='"
                        + listaRegistro.get(i).get("SEquipoId") + "'");
                while (consultaAS400.next()) {
                    if (consultaAS400.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                        Contador++;
                        System.out.println("Registros: " + Contador);
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        System.out.println("Registro No Encontrado. NUMEQU: " + SEquipoId);
                        TrazaLista.add("Registro No Encontrado.  NUMEQU='" + listaRegistro.get(i).get("SEquipoId") + "'");
                    }
                }
                conexionAS400.close();
            }
            conexionSqlServer.close();
        }
        ResultSet resultadoConsulta = conexionSqlServer1.Consultar("select top 1 NRegistrosAfectados from S_SDI.TBL_LogMasivas where SEntidad = 'EquipoNevera' and SObjetoAfectado = 'TBL_EquipoNevera' ORDER BY DFechaInicio desc");
        while (resultadoConsulta.next()) {
            texto = resultadoConsulta.getString("NRegistrosAfectados");
        }

        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Datos Enviados Celuweb  " + texto);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);

        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
            Utils.Imprimir(TrazaLista);
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        return respuesta;
    }

    public static String ValidacionMasivaMovimientoEnvase(Response lastResponse, String Api) throws SQLException {
        String respuesta = null;
        int DatosObtenidosApi = 0, contador = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        List<HashMap<String, Object>> listaRegistro;
        int consultaSAP;
        String NCantidad, texto = null;
        String consultaSQL = "Select NCantidad from SDI.TBL_MovimientoEnvase";
        listaRegistro = ConsultasBD.obtenerRegistro(consultaSQL, "SQLSERVER");
        for (int i = 0; i < listaRegistro.size(); i++) {
            NCantidad = listaRegistro.get(i).get("NCantidad").toString().trim();
            ResultSet consultaAS400 = null;
            consultaSAP = conexionSAP.Consultar("/BEV1/EMLGBSD", "BNQ1", "BNQ1 = '"
                    + listaRegistro.get(i).get("NCantidad") + "'");
            DatosObtenidosApi = DatosObtenidosApi + 1;
            if (consultaSAP > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
                DatosValidadosDestino = DatosValidadosDestino + 1;
                contador++;
                System.out.println("Registros: " + contador);
            } else {
                consultaAS400 = conexionAS400.Consultar("select count(*) FROM VEM02LIP.VWCLTPRES WHERE  SALDOACT='"
                        + listaRegistro.get(i).get("NCantidad") + "'");
                while (consultaAS400.next()) {
                    if (consultaAS400.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                        contador++;
                        System.out.println("Registros: " + contador);
                    } else {
                        consultaAS400 = conexionAS400Central.Consultar("select count(*) FROM VEM02LIP.VWCLTPRES WHERE  SALDOACT='"
                                + listaRegistro.get(i).get("NCantidad") + "'");
                        while (consultaAS400.next()) {
                            if (consultaAS400.getInt(1) > 0) {
                                DatosValidadosOrigen = DatosValidadosOrigen + 1;
                                contador++;
                                System.out.println("Registros: " + contador);
                            }else{
                                DatosFallidosOrigen = DatosFallidosOrigen + 1;
                                TrazaLista.add("Registro No Encontrado.  SALDOACT='"
                                        + listaRegistro.get(i).get("NCantidad") + "'");
                            }
                        }
                    }
                }
                conexionAS400.close();
            }
            conexionSqlServer.close();
        }
     /*   ResultSet resultadoConsulta = conexionSqlServer1.Consultar("select  top 1  NRegistrosAfectados from S_SDI.TBL_LogMasivas where SEntidad = 'MovimientoEnvase'  and SObjetoAfectado = 'TBL_MovimientoEnvase' ORDER BY DFechaInicio desc");
        while (resultadoConsulta.next()) {
            texto = resultadoConsulta.getString("NRegistrosAfectados");
            conexionSqlServer1.close();
        }*/

        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Datos Enviados Celuweb  " + texto);
        TrazaLista.add("\n Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi + "\n");
        TrazaLista.add(" Datos " + Api + " Validados Origen SAP / AS400." + DatosValidadosOrigen);
        TrazaLista.add("\nDatos " + Api + " Fallidos Origen SAP / AS400." + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("\n Datos  " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("\n Datos  " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino + "\n");

        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            TrazaLista.add("\n\n");
            Utils.Imprimir(TrazaLista);
        }
        ValidacionesDB Obj = new ValidacionesDB();
        Serenity.setSessionVariable("Respuesta").to("");
        setSessionVariable("Respuesta").to(TrazaLista);
        return respuesta;
    }

    public static String ValidacionRutasCentroId(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, rutaId = null, centroId = null, nombre, tipoPlanId;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        int datosCorrectosEquivalencia = 0, datosFallidosEquivalencia = 0, datosObtenidosMetadata = 0, contador = 0;
        String canonicoId = null, as400Id = null, sapId = null;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API RutaCentroId")) {
                Object pathdrutaID = lastResponse.path("rutas[" + i + "].rutaId");
                canonicoId = lastResponse.path("metadata[" + i + "].canonicoId");
                //Si esta vacio termina el for
                if (pathdrutaID == null) {
                    break;
                }
                if (canonicoId != null) {
                    datosObtenidosMetadata = datosObtenidosMetadata + 1;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                rutaId = pathdrutaID.toString();
                centroId = lastResponse.path("rutas.centro.centroId").toString();
                nombre = lastResponse.path("rutas[" + i + "].centro.nombre").toString();
                tipoPlanId = lastResponse.path("rutas[" + i + "].tipoPlanId").toString().trim();

                as400Id = lastResponse.path("metadata[" + i + "].as400Id");
                sapId = lastResponse.path("metadata[" + i + "].sapId");
            } else {
                i = 1000;
                Object pathdrutaID = lastResponse.path("rutas.rutaId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                rutaId = pathdrutaID.toString();
                centroId = lastResponse.path("rutas.centroId").toString();
                nombre = lastResponse.path("rutas.centroId.nombre").toString();
                tipoPlanId = lastResponse.path("rutas.tipoPlanId").toString().trim();

                canonicoId = lastResponse.path("metadata.canonicoId");
                as400Id = lastResponse.path("metadata.as400Id");
                sapId = lastResponse.path("metadata.sapId");
            }

            //VALIDACIÓN ORIGEN
            ResultSet resultadoConsulta = null;
            int fila = conexionSAP.Consultar("T001W", "NAME1", "NAME1 = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
                contador++;
                System.out.println("Registros: " + contador);
            } else {
                //VALIDACIÓN ORIGEN
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEMXXWM.TPWPLANT WHERE  NOMPLT='" + nombre + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                        contador++;
                        System.out.println("Registros: " + contador);
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.  NOMPLT='" + nombre + "'");
                    }
                }
            }


            //VALIDACIÓN DESTINO
            resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_Ruta WHERE GRutaId='" + rutaId + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.   GRutaId='" + rutaId + "'");
                }
            }

            //Validacion Metadata
            if (canonicoId != null) {
                resultadoConsulta = conexionSqlServer.Consultar("SELECT SAS400Id, SSApId FROM SDI.TBL_EquivalenciaDetalle WHERE GPKId = (SELECT GRutaId FROM SDI.TBL_Ruta  WHERE GRutaId = '" + canonicoId + "')");
                while (resultadoConsulta.next()) {
                    if (as400Id == null || sapId == null) {
                        if (as400Id == null) {
                            if (resultadoConsulta.getString(1) == null && resultadoConsulta.getString(2).equals(sapId)) {
                                datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                            } else {
                                datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                            }
                        } else {
                            if (resultadoConsulta.getString(1).equals(as400Id) && resultadoConsulta.getString(2) == null) {
                                datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                            } else {
                                datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                            }
                        }
                    } else if (resultadoConsulta.getString(1).equals(as400Id) && resultadoConsulta.getString(2).equals(sapId)) {
                        datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                    } else {
                        datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                    }
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        System.out.println("Datos " + Api + " Metadata: " + datosObtenidosMetadata);
        System.out.println("Datos Validados Equivalencia: " + datosCorrectosEquivalencia);
        System.out.println("Datos Fallidos Equivalencia: " + datosFallidosEquivalencia);

        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Obtenidos Metadata. " + datosObtenidosMetadata);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Metadata. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Metadata. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            Utils.Imprimir(TrazaLista);
        }
        conexionSqlServer.close();
        return respuesta;
    }

    public static String ValidacionRutas(Response lastResponse, String Api) throws SQLException {
        String respuesta = null, rutaId = null, centroId = null, nombre, tipoPlanId;
        int DatosObtenidosApi = 0, DatosValidadosOrigen = 0, DatosFallidosOrigen = 0, DatosValidadosDestino = 0, DatosFallidosDestino = 0;
        int datosCorrectosEquivalencia = 0, datosFallidosEquivalencia = 0, datosObtenidosMetadata = 0, contador = 0;
        String canonicoId = null, as400Id = null, sapId = null;
        for (int i = 0; i < 1000; i++) {
            if (Api.equals("API Rutas")) {
                Object pathdrutaID = lastResponse.path("rutas[" + i + "].rutaId");
                canonicoId = lastResponse.path("metadata[" + i + "].canonicoId");
                //Si esta vacio termina el for
                if (pathdrutaID == null) {
                    break;
                }
                if (canonicoId != null) {
                    datosObtenidosMetadata = datosObtenidosMetadata + 1;
                }
                DatosObtenidosApi = DatosObtenidosApi + 1;
                rutaId = pathdrutaID.toString();
                centroId = lastResponse.path("rutas.centro.centroId").toString();
                nombre = lastResponse.path("rutas[" + i + "].centro.nombre").toString();
                tipoPlanId = lastResponse.path("rutas[" + i + "].tipoPlanId").toString().trim();

                as400Id = lastResponse.path("metadata[" + i + "].as400Id");
                sapId = lastResponse.path("metadata[" + i + "].sapId");
            } else {
                i = 1000;
                Object pathdrutaID = lastResponse.path("rutas.rutaId");
                DatosObtenidosApi = DatosObtenidosApi + 1;
                rutaId = pathdrutaID.toString();
                centroId = lastResponse.path("rutas.centroId").toString();
                nombre = lastResponse.path("rutas.centroId.nombre").toString();
                tipoPlanId = lastResponse.path("rutas.tipoPlanId").toString().trim();

                canonicoId = lastResponse.path("metadata.canonicoId");
                as400Id = lastResponse.path("metadata.as400Id");
                sapId = lastResponse.path("metadata.sapId");
            }

            //VALIDACIÓN ORIGEN
            ResultSet resultadoConsulta = null;
            int fila = conexionSAP.Consultar("T001W", "NAME1", "NAME1 = '" + nombre + "'");
            if (fila > 0) {
                DatosValidadosOrigen = DatosValidadosOrigen + 1;
                contador++;
                System.out.println("Registros: " + contador);
            } else {
                //VALIDACIÓN ORIGEN
                resultadoConsulta = conexionAS400.Consultar("select count(*) FROM VEMXXWM.TPWPLANT WHERE  NOMPLT='" + nombre + "'");
                while (resultadoConsulta.next()) {
                    if (resultadoConsulta.getInt(1) > 0) {
                        DatosValidadosOrigen = DatosValidadosOrigen + 1;
                        contador++;
                        System.out.println("Registros: " + contador);
                    } else {
                        DatosFallidosOrigen = DatosFallidosOrigen + 1;
                        TrazaLista.add("Registro No Encontrado.  NOMPLT='" + nombre + "'");
                    }
                }
            }

            //VALIDACIÓN DESTINO
            resultadoConsulta = conexionSqlServer.Consultar("SELECT COUNT(*) FROM SDI.TBL_Ruta WHERE GRutaId='" + rutaId + "'");
            while (resultadoConsulta.next()) {
                if (resultadoConsulta.getInt(1) > 0) {
                    DatosValidadosDestino = DatosValidadosDestino + 1;
                } else {
                    DatosFallidosDestino = DatosFallidosDestino + 1;
                    TrazaLista.add("Registro No Encontrado.   GRutaId='" + rutaId + "'");
                }
            }

            //Validacion Metadata
            if (canonicoId != null) {
                resultadoConsulta = conexionSqlServer.Consultar("SELECT SAS400Id, SSApId FROM SDI.TBL_EquivalenciaDetalle WHERE GPKId = (SELECT GRutaId FROM SDI.TBL_Ruta  WHERE GRutaId = '" + canonicoId + "')");
                while (resultadoConsulta.next()) {
                    if (as400Id == null || sapId == null) {
                        if (as400Id == null) {
                            if (resultadoConsulta.getString(1) == null && resultadoConsulta.getString(2).equals(sapId)) {
                                datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                            } else {
                                datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                            }
                        } else {
                            if (resultadoConsulta.getString(1).equals(as400Id) && resultadoConsulta.getString(2) == null) {
                                datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                            } else {
                                datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                            }
                        }
                    } else if (resultadoConsulta.getString(1).equals(as400Id) && resultadoConsulta.getString(2).equals(sapId)) {
                        datosCorrectosEquivalencia = datosCorrectosEquivalencia + 1;
                    } else {
                        datosFallidosEquivalencia = datosFallidosEquivalencia + 1;
                    }
                }
            }

        }
        System.out.println("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        System.out.println("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        System.out.println("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        System.out.println("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        System.out.println("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        System.out.println("Datos " + Api + " Metadata: " + datosObtenidosMetadata);
        System.out.println("Datos Validados Equivalencia: " + datosCorrectosEquivalencia);
        System.out.println("Datos Fallidos Equivalencia: " + datosFallidosEquivalencia);

        TrazaLista.add("Datos " + Api + " Obtenidos Request. " + DatosObtenidosApi);
        TrazaLista.add("Datos " + Api + " Obtenidos Metadata. " + datosObtenidosMetadata);
        TrazaLista.add("Datos " + Api + " Validados Origen SAP / AS400. " + DatosValidadosOrigen);
        TrazaLista.add("Datos " + Api + " Fallidos Origen SAP / AS400. " + DatosFallidosOrigen);
        TrazaLista.add("Datos " + Api + " Validados Destino SDI. " + DatosValidadosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Destino SDI. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Validados Metadata. " + DatosFallidosDestino);
        TrazaLista.add("Datos " + Api + " Fallidos Metadata. " + DatosFallidosDestino);
        if (DatosObtenidosApi == DatosValidadosDestino) {
            respuesta = "Data " + Api + " Validada Exitosamente";
            TrazaLista.add(respuesta);
        } else {
            respuesta = "Validacion de Data " + Api + " Fallida";
            TrazaLista.add(respuesta);
            Utils.Imprimir(TrazaLista);
        }
        conexionSqlServer.close();
        return respuesta;
    }

}