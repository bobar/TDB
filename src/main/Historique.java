package main;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

public class Historique {

    public static class Entry {
	public String client;
	public double price;
	public String banque;
	public String admin;
	public String comment;
	public String date;
    }

    public static LinkedList<Entry> getHistorique(MainWindow parent, Trigramme trigramme, int limit)
	    throws Exception {
	Statement stmt = parent.connexion.createStatement();
	String query =
		"SELECT price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			+ " FROM transactions as tr " + " JOIN accounts as ac1 ON ac1.id=tr.id "
			+ " JOIN accounts as ac2 ON ac2.id=tr.id2 "
			+ " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin " + " WHERE tr.id="
			+ trigramme.id + " ORDER BY date DESC";
	if (limit >= 0) query += " LIMIT " + limit;
	ResultSet rs = stmt.executeQuery(query);
	LinkedList<Entry> res = new LinkedList<Entry>();
	while (rs.next()) {
	    Entry zou = new Entry();
	    zou.client = rs.getString("t1");
	    zou.price = (double) rs.getInt("p") / 100;
	    zou.banque = rs.getString("t2");
	    if(zou.banque.equals(parent.banqueBob.trigramme)){
		zou.banque="";
	    }
	    zou.admin = rs.getString("t3");
	    zou.comment = rs.getString("c");
	    zou.date =
		    rs.getString("date").substring(0, Math.min(16, rs.getString("date").length()));
	    res.add(zou);
	}
	return res;
    }
    
    public static LinkedList<Entry> getOldHistorique(MainWindow parent, Trigramme trigramme, int limit)
	    throws Exception {
	Statement stmt = parent.connexion.createStatement();
	String query =
		"SELECT price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			+ " FROM transactions_history as tr " + " JOIN accounts as ac1 ON ac1.id=tr.id "
			+ " JOIN accounts as ac2 ON ac2.id=tr.id2 "
			+ " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin " + " WHERE tr.id="
			+ trigramme.id + " ORDER BY date DESC";
	if (limit >= 0) query += " LIMIT " + limit;
	ResultSet rs = stmt.executeQuery(query);
	LinkedList<Entry> res = new LinkedList<Entry>();
	while (rs.next()) {
	    Entry zou = new Entry();
	    zou.client = rs.getString("t1");
	    zou.price = (double) rs.getInt("p") / 100;
	    zou.banque = rs.getString("t2");
	    if(zou.banque.equals(parent.banqueBob.trigramme)){
		zou.banque="";
	    }
	    zou.admin = rs.getString("t3");
	    zou.comment = rs.getString("c");
	    zou.date =
		    rs.getString("date").substring(0, Math.min(16, rs.getString("date").length()));
	    res.add(zou);
	}
	return res;
    }
}
