package main;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

    public int id;
    public int price;
    public String comment;
    public int admin_id;
    public String date;
    public int id2;

    public Transaction(int id, int price, String comment, Admin admin, String date, int id2) {
	this.id = id;
	this.price = price;
	this.comment = comment;
	if (admin == null) {
	    this.admin_id = 0;
	} else {
	    this.admin_id = admin.id;
	}
	if (date == null) {
	    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    this.date = formater.format(new Date());
	} else {
	    this.date = date;
	}
	this.id2 = id2;
    }

    public void WriteToDB(MainWindow parent) throws Exception {
	Statement stmt = parent.connexion.createStatement();
	if (price > 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + price + " WHERE id=" + id);
	} else if (price < 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + (-price) + " WHERE id="
		    + id);
	}
	stmt.executeUpdate("INSERT INTO transactions (id,price,comment,admin,date,id2) VALUES ("
		+ id + "," + price + ",'" + comment + "'," + admin_id + ",'" + date + "'," + id2
		+ ")");
	stmt.executeUpdate("INSERT INTO transactions (id,price,comment,admin,date,id2) VALUES ("
		+ id2 + "," + (-price) + ",'" + comment + "'," + admin_id + ",'" + date + "'," + id
		+ ")");
	if (price > 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + price + " WHERE id=" + id2);
	} else if (price < 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + (-price) + " WHERE id="
		    + id2);
	}
    }

    public void annuler(Connection connexion) throws Exception {
	Statement stmt = connexion.createStatement();
	if (price > 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + price + " WHERE id=" + id);
	} else if (price < 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + (-price) + " WHERE id="
		    + id);
	}
	stmt.executeUpdate("DELETE FROM transactions WHERE id=" + id + " AND price=" + price
		+ " AND comment='" + comment + "' AND admin=" + admin_id + " AND date='" + date
		+ "' AND id2=" + id2);
	if (price > 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + price + " WHERE id=" + id2);
	} else if (price < 0) {
	    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + (-price) + " WHERE id="
		    + id2);
	}
    }

}
