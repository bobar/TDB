package main;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

public class Historique {

  public static class Entry {
    public String client;
    public double price;
    public String banque;
    public String admin;
    public String comment;
    public String date;

    public String localDate() throws ParseException {
      SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

      return localDateFormat.format(utcDateFormat.parse(date));
    }
  }

  public static LinkedList<Entry> getHistorique(MainWindow parent, Trigramme trigramme, int limit)
      throws Exception {
    return loadHistorique(parent, trigramme, limit, "transactions");
  }

  public static LinkedList<Entry> getOldHistorique(MainWindow parent, Trigramme trigramme, int limit)
      throws Exception {
    return loadHistorique(parent, trigramme, limit, "transactions_history");
  }

  public static LinkedList<Entry> loadHistorique(MainWindow parent, Trigramme trigramme, int limit, String table)
      throws Exception {
    Statement stmt = parent.connexion.createStatement();
    String query =
        "SELECT amount as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
            + " FROM " + table + " as tr " + " JOIN accounts as ac1 ON ac1.id=tr.buyer_id "
            + " JOIN accounts as ac2 ON ac2.id=tr.receiver_id "
            + " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin " + " WHERE tr.buyer_id=" + trigramme.id
            + " OR tr.receiver_id=" + trigramme.id
            + " ORDER BY date DESC";
    if (limit >= 0)
      query += " LIMIT " + limit;
    ResultSet rs = stmt.executeQuery(query);
    LinkedList<Entry> res = new LinkedList<Entry>();
    while (rs.next()) {
      Entry zou = new Entry();
      int amount = rs.getInt("p");
      String buyer = rs.getString("t1");
      boolean reverse = (buyer.equals(trigramme.trigramme));
      zou.client = (reverse) ? rs.getString("t1") : rs.getString("t2");
      zou.price = (double) ((reverse) ? amount : -amount) / 100;
      zou.banque = (reverse) ? rs.getString("t2") : rs.getString("t1");
      if (zou.banque.equals(parent.banqueBob.trigramme)) {
        zou.banque = "";
      }
      zou.admin = rs.getString("t3");
      zou.comment = rs.getString("c");
      zou.date = rs.getString("date").substring(0, Math.min(19, rs.getString("date").length()));
      res.add(zou);
    }
    return res;
  }
}
