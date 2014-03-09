package main;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

    public int id;
    public int price;
    public String comment;
    public int admin;
    public String date;
    public int id2;

    public Transaction(int id, int price, String comment, int admin, String date, int id2) {
	this.id = id;
	this.price = price;
	this.comment = comment;
	this.admin = admin;
	if (date == null) {
	    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    this.date = formater.format(new Date());
	} else {
	    this.date = date;
	}
	this.id2 = id2;
    }

    public void WriteToDB(Connection connexion) throws Exception {
	Statement stmt = connexion.createStatement();
	if (price > 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + price + " WHERE id=" + id);
	} else if (price < 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + (-price) + " WHERE id="
		    + id);
	}
	stmt.executeUpdate("INSERT INTO transactions (id,price,comment,admin,date,id2) VALUES ("
		+ id + "," + price + ",'" + comment + "'," + admin + ",'" + date + "'," + id2 + ")");
	if (price > 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + price + " WHERE id=" + id2);
	} else if (price < 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + (-price) + " WHERE id="
		    + id2);
	}
    }

}
