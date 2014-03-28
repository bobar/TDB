package tdb.trig.db;

import java.sql.SQLException;

import main.TrigException;
import main.Trigramme;

public interface TrigrammeDatabaseInterface {
	
	public void OpenTrigrammeDatabase() throws ClassNotFoundException, SQLException;
	
	public Trigramme OpenTrigramme(String TRI) throws SQLException, TrigException;

}
