package co.com.postobon.qa.proyectoapis.utils;
import java.sql.Connection;
import java.sql.DriverManager;

/*Objetivo : Conectarnos a la Base de datos MySQL*/

public class ConexionMySQL {
	String host;
	String user;
	String password;
	String dbname;
	int port;
	protected Connection con;
	
	public ConexionMySQL(String host,String user,String password) {
		this.host=host;
		this.user=user;
		this.password=password;
		this.dbname="";
		this.port=3306;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.con = DriverManager.getConnection("jdbc:mysql://"+this.host+":"+this.port+"/"+this.dbname, this.user,this.password);
		} catch (Exception e) {
			System.out.println("========================================================================");
			e.printStackTrace();
			System.out.println("========================================================================");
		}
	}

}
