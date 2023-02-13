package co.com.postobon.qa.proyectoapis.models;

public class ConexionFTPModel {

    private String userName;
    private int port;
    private String host;
    private String password;

    public ConexionFTPModel(String userName, int port, String host, String password) {
        this.userName = userName;
        this.port = port;
        this.host = host;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public int getPort() {
        return port;
    }
}
