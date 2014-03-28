package tdb.trig;

import java.sql.SQLException;

import main.Trigramme;

public class TrigrammeSQLDatabase extends SQLDataBase implements TrigrammeDatabaseInterface  {

	public TrigrammeSQLDatabase(String host, String user, String password,
			String base) throws ClassNotFoundException, SQLException {
		
		super(host, user, password, base);
		super.OpenSQLDatabase();
		
	}

	@Override
	public Trigramme OpenTrigramme(String TRI) {
		// TODO Auto-generated method stub
		return null;
	}

}
