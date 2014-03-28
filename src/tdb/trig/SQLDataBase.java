package tdb.trig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class SQLDataBase {
	
	final int SQLTIMEOUT=5000; 

	public String sqldb_host;
		
	public String sqldb_user;
	public String sqldb_password;
	
	public String sqldb_base;
	
	private Connection connexion;
	
	public SQLDataBase(String host,String user,String password,String base){
		
		this.sqldb_host=host;
		
		this.sqldb_user=user;
		this.sqldb_password=password;
		
		this.sqldb_base=base;
		
	}
	
	public void OpenSQLDatabase() throws ClassNotFoundException, SQLException{
		
		Class.forName("com.mysql.jdbc.Driver");
		
		this.connexion=
				DriverManager.getConnection("jdbc:mysql://" + this.sqldb_host + "/" + this.sqldb_base,
						this.sqldb_user,
						this.sqldb_password);
	}
	
	public void CloseSQLDatabase() throws SQLException{
		this.connexion.close();
	}
	
	public boolean GetStatus() throws SQLException{
		return this.connexion.isValid(SQLTIMEOUT);
	}
}
