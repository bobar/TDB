package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

	static String user_id;
	static String password;	
	static String host_name;
	static String basename;
	
	Connection connexion;

	private String getExecutionPath() {
		String absolutePath =
				getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
		absolutePath = absolutePath.replaceAll("%20", "\\ "); // Surely need to do this here
		if (absolutePath.substring(absolutePath.length() - 3).equals("bin")) {
			absolutePath += "/.."; // Hack sordide pour l'éxécution dans Eclipse
		}
		return absolutePath;
	}

	public void LoadDatabaseConfiguration(String config_file_path) throws IOException{
		
		Properties properties=null;
		InputStream ips=new FileInputStream(config_file_path);
		
		properties.load(ips);
		
		Database.user_id=properties.getProperty("user");
		Database.password=properties.getProperty("password");
		Database.host_name=properties.getProperty("host");
		Database.basename=properties.getProperty("tdbase");
	}
	
	public void connecter() throws SQLException, ClassNotFoundException, IOException {
		Class.forName("com.mysql.jdbc.Driver");
		String user = "root";
		String passwd = "plap";
		String host = "localhost";
		String db = "tdb";
		
		Properties properties=new Properties();
		
		
		String absolutePath = this.getExecutionPath();
		InputStream ips = new FileInputStream(absolutePath + "/src/TDB.config");
		System.out.println(absolutePath + "/src/TDB.config");
		
		properties.load(ips);
		
		user=properties.getProperty("dbUser");
		passwd=properties.getProperty("dbPassword");
		host=properties.getProperty("dbHost");
		db=properties.getProperty("dbName");
		
		
		this.connexion =
				DriverManager.getConnection("jdbc:mysql://" + host + "/" + db, user, passwd);
	}

	public boolean deconnecter() throws SQLException {
		connexion.close();
		return true;
	}

}
