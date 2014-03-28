package tdb.trig.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.TrigException;
import main.Trigramme;

public class TrigrammeSQLDatabase extends SQLDataBase implements TrigrammeDatabaseInterface  {

	public TrigrammeSQLDatabase(String host, String user, String password,
			String base) throws ClassNotFoundException, SQLException {
		
		super(host, user, password, base);
		
		
	}

	@Override
	public Trigramme OpenTrigramme(String TRI) throws SQLException, TrigException {
		
		ResultSet rs=this.ExecuteQuery("SELECT * FROM accounts WHERE trigramme='" + TRI + "'");
		if (rs.next()) {
			int id = rs.getInt("id");
			String trigramme = rs.getString("trigramme");
			String name = rs.getString("name");
			String first_name = rs.getString("first_name");
			String nickname = rs.getString("nickname");
			String casert = rs.getString("casert");
			int status = rs.getInt("status");
			int promo = rs.getInt("promo");
			String picture = rs.getString("picture");
			int balance = rs.getInt("balance");
			int turnover = rs.getInt("turnover");
			if (rs.next()) {
				throw new TrigException("Trigramme pas unique");
			}
			return new Trigramme(null,
					trigramme,
					name,
					first_name,
					nickname,
					casert,
					status,
					promo,
					picture,
					picture, balance,
					turnover);
		} else {
			throw new TrigException("Trigramme inconnu : " + TRI);
		}
	}

	@Override
	public void OpenTrigrammeDatabase() throws ClassNotFoundException, SQLException {
		super.OpenSQLDatabase();
		
	}

}
