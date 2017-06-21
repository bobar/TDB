package main;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Transaction {

  public int id;
  public int price;
  public String comment;
  public int admin_id;
  public String date;
  public int id2;
  public boolean is_clopes = false;

  public Transaction(int id, int price, String comment, Admin admin, String date, int id2) {
    boolean reverse = (price < 0);
    this.id = reverse ? id2 : id;
    this.id2 = reverse ? id : id2;
    this.price = reverse ? -price : price;
    this.comment = comment;
    if (admin == null) {
      this.admin_id = 0;
    } else {
      this.admin_id = admin.id;
    }
    if (date == null) {
      SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      formater.setTimeZone(TimeZone.getTimeZone("UTC"));

      this.date = formater.format(new Date());
    } else {
      this.date = date;
    }
  }

  public void WriteToDB(MainWindow parent) throws Exception {
    Statement stmt = parent.connexion.createStatement();
    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + price + ", turnover=turnover+"
        + price + " WHERE id=" + id);
    if (is_clopes) {
      stmt.execute("UPDATE accounts SET total_clopes = total_clopes+" + price + " WHERE id="
          + id);
    }
    stmt.executeUpdate("INSERT INTO transactions (buyer_id,amount,comment,admin,date,receiver_id) VALUES (" + id
        + "," + price + ",'" + comment + "'," + admin_id + ",'" + date + "'," + id2 + ")");
    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + price + " WHERE id=" + id2);
  }

  public void annuler(Connection connexion) throws Exception {
    Statement stmt = connexion.createStatement();
    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + price + ", turnover=turnover-"
        + price + " WHERE id=" + id);
    if (is_clopes) {
      stmt.execute("UPDATE accounts SET total_clopes = total_clopes-" + price + " WHERE id="
          + id);
    }
    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + price + " WHERE id=" + id2);
    String query = "UPDATE transactions SET amount=0, comment=CONCAT(comment, \" (Annulée, valait " +
      ((double) price / 100) + " €)\") WHERE buyer_id=" + id + " AND receiver_id=" + id2 + " AND date=\"" + date + "\"";
    stmt.executeUpdate(query);
  }
}
