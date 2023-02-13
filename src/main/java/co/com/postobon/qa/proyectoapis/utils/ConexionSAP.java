package co.com.postobon.qa.proyectoapis.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Properties;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class ConexionSAP {
    static String   IP="10.1.108.10", //IP or HOST
            USER="CE_YRUIZM", // user name of SAP
            PASSWORD="Prueba123*", // password of SAP
            CLIENT="040", //mandant in sap
            //CLIENT="100", //mandant in sap
            SYSNR="03", // instance number
            LANG="es"; // language (es or en)
            static  JCoRepository sapRepository;
            static JCoDestination destination;


    private static void Conect() {



        try {
            //Sistema
            String DESTINATION_NAME1 = "ALQ";
            Properties connectProperties = new Properties();
            connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,   IP);
            connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,    SYSNR);
            connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,   CLIENT);
            connectProperties.setProperty(DestinationDataProvider.JCO_USER,     USER);
            connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,   PASSWORD);
            connectProperties.setProperty(DestinationDataProvider.JCO_LANG,     LANG);
            createDestinationDataFile(DESTINATION_NAME1,connectProperties);


            destination = JCoDestinationManager.getDestination("ALQ");

            sapRepository = destination.getRepository();

/*
            JCoFunctionTemplate template2 = sapRepository.getFunctionTemplate("RFC_READ_TABLE");
            System.out.println("Getting template");
            JCoFunction function2 = template2.getFunction();
            function2.getImportParameterList().setValue("QUERY_TABLE", "TVV5T");
            //function2.getImportParameterList().setValue("DELIMITER", ",");
            //function2.getImportParameterList().setValue("ROWSKIPS", Integer.valueOf(0));
            //function2.getImportParameterList().setValue("ROWCOUNT", Integer.valueOf(0));


            JCoTable returnOptions = function2.getTableParameterList().getTable("OPTIONS");

            System.out.println("Setting CONDICIONES");
            //CONDICIÓN 1
            //returnOptions.appendRow();
            //returnOptions.setValue("TEXT", "KVGR5 = '03'");
            //CONDICIÓN 2
            //returnOptions.appendRow();
            //returnOptions.setValue("TEXT", "AND SPRAS = 'S'");

            //CONDICIONES 1 y 2 Juntas
            returnOptions.appendRow();
            returnOptions.setValue("TEXT", "KVGR5 = '03' AND SPRAS = 'S'");

            System.out.println("Setting CONDICIONES");
            JCoTable returnFields = function2.getTableParameterList().getTable("FIELDS");
            //Nombre de Los Campos A Capturar
            returnFields.appendRow();
            returnFields.setValue("FIELDNAME", "KVGR5");
            returnFields.appendRow();
            returnFields.setValue("FIELDNAME", "BEZEI");

            function2.execute(destination);

            JCoTable jcoTabled = function2.getTableParameterList().getTable("DATA");


            int numRows = jcoTabled.getNumRows();
            System.out.println("numRows > " + numRows);

            if (numRows > 0) {
                System.out.println("numRows > " + numRows);
            }

        } catch (JCoException ex) {
            System.out.println("exception "+ex.toString());
            */
        } catch(Exception ex) {
            System.out.println("exception "+ex.toString());
        }
    }

    private static void createDestinationDataFile(String destinationName, Properties connectProperties)
    {
        File destCfg = new File(destinationName+".jcoDestination");
        try
        {
            FileOutputStream fos = new FileOutputStream(destCfg, false);
            connectProperties.store(fos, "for tests only !");
            fos.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to create the destination files", e);
        }
    }

    static int Consultar(String strTabla, String strCampo, String strCondicion/*, String strCondicion2*/)
    {
        Conect();
        int numRows=0;
        try {
            //Sistema

            JCoFunctionTemplate template2 = sapRepository.getFunctionTemplate("RFC_READ_TABLE");
           // System.out.println("Getting template");
            JCoFunction function2 = template2.getFunction();
            function2.getImportParameterList().setValue("QUERY_TABLE", strTabla);

            JCoTable returnOptions = function2.getTableParameterList().getTable("OPTIONS");

            //System.out.println("Setting CONDICIONES");

            //CONDICIONES
            returnOptions.appendRow();
            returnOptions.setValue("TEXT", strCondicion);
            returnOptions.setValue("TEXT", strCondicion);

            //System.out.println("Setting FILAS");
            JCoTable returnFields = function2.getTableParameterList().getTable("FIELDS");
            //Nombre de Los Campos A Capturar
            returnFields.appendRow();
            returnFields.setValue("FIELDNAME", strCampo);

            function2.execute(destination);

            JCoTable jcoTabled = function2.getTableParameterList().getTable("DATA");

            numRows = jcoTabled.getNumRows();

            if (numRows > 0) {
                //System.out.println("Filas Conseguidas > " + numRows);
            }

            //Cierre de Conexion
            JCoContext.end(destination);
        } catch (JCoException ex) {
            System.out.println("exception "+ex.toString());
        } catch(Exception ex) {
            System.out.println("exception "+ex.toString());
        }


    return numRows;
    }

}
