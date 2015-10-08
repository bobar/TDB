package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

  Connection connexion;

  public void connecter(Properties properties) throws SQLException, ClassNotFoundException, IOException {
    Class.forName("com.mysql.jdbc.Driver");
    String user = properties.getProperty("dbUser", "root");
    String passwd = properties.getProperty("dbPassword", "");
    String host = properties.getProperty("dbHost", "localhost");
    String db = properties.getProperty("dbName", "tdb");
    this.connexion = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db, user, passwd);
  }

  public boolean deconnecter() throws SQLException {
    connexion.close();
    return true;
  }

}
