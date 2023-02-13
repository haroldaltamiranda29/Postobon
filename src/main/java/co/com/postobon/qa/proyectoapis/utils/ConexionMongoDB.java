package co.com.postobon.qa.proyectoapis.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.io.FileInputStream;
import java.sql.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

//Documentación
//https://docs.mongodb.com/drivers/java/sync/current/
public class ConexionMongoDB {
	 //Variables Necesarias
	/*
	 *ClusterMongo: 
	 *Ejemplo. cluster0.mongodb.net  / 
	 *Establezca serverSelectionTryOnce en para indicar al false controlador C que realice la selección del servidor hasta el límite de tiempo definido por serverSelectionTimeoutMS.
	 *Bajar los serverSelectionTimeoutMS a 15000del valor predeterminado de 30000. Las elecciones de MongoDB suelen tardar 10 segundos, pero pueden ser tan rápidas como 5 segundos en Atlas. 
	 *Establecer este valor en 15 segundos ( 15000milisegundos) cubre el límite superior de la elección más el tiempo adicional de latencia.
	 */
    private String strClusterMongo="";
    private String strUsuario="";
    private String strPass="";
    private Connection conexion;
    
    //Se Puede usar un Archivo Properties
    /*
     private void cargarDatos() {
		try {
			Properties propiedad= new Properties();
			propiedad.load(new FileInputStream("src/test/resources/dataInicioMongoDB.properties"));
			strUsuario = propiedad.getProperty("Usuario");
			strPass = propiedad.getProperty("Pass");
			strClusterMongo = propiedad.getProperty("ClusterMongo");
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
    */
    
    
    
    public void conectarmeMongoDB() {  
    	//cargarDatos();
    	

    	ConnectionString connString = new ConnectionString("mongodb+srv://"+strUsuario+":"+strPass+"@"+strClusterMongo+"/?serverSelectionTryOnce=false&serverSelectionTimeoutMS=15000&w=majority");
    	//Line 57 a 61, permite que se pueda escribir o no en la BD; true=RechazaInsertarDatos, false=PermiteInsertarDatos
    	MongoClientSettings settings = MongoClientSettings.builder()
    	    .applyConnectionString(connString)
    	    .retryWrites(true)
    	    .build();
    	MongoClient mongoClient = MongoClients.create(settings);
    	MongoDatabase database = mongoClient.getDatabase("test");
    	
    }
    
   
}
