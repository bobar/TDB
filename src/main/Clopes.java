package main;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

public class Clopes {

  public int id;
  private String marque;
  private int prix;
  private int quantite;

  public MainWindow parent;

  public Clopes(MainWindow parent, String marque) throws Exception {
    this.parent = parent;
    Statement stmt = parent.connexion.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM clopes WHERE marque='" + marque + "'");
    if (rs.next()) {
      id = rs.getInt("id");
      this.marque = marque;
      quantite = rs.getInt("quantite");
      prix = rs.getInt("prix");
      if (rs.next()) {
        throw new DBException("Cette marque n'est pas unique.");
      }
    } else {
      throw new TDBException("Clopes inconnues : " + marque);
    }
  }

  public Clopes(MainWindow parent, String marque, int prix) throws Exception {
    this.parent = parent;
    this.marque = marque;
    this.prix = prix;
    this.quantite = 0;
  }

  public int prix() {
    return prix;
  }

  public String marque() {
    return marque;
  }

  public int quantite() {
    return quantite;
  }

  public void creer(Admin admin) throws Exception {
    Statement stmt = parent.connexion.createStatement();
    stmt.executeUpdate("INSERT INTO clopes (marque,prix,quantite) VALUES ('" + marque + "'," + prix
        + ",0)");
    stmt.close();
    Transaction transaction =
        new Transaction(parent.banqueBob.id, 0, "Création clopes : " + marque, admin, null,
            parent.banqueBob.id);
    transaction.WriteToDB(parent);
  }

  public void vendre(Trigramme trigramme, Admin admin, int quantite) throws Exception {
    Transaction transaction =
        new Transaction(trigramme.id, quantite * prix, quantite + " " + marque, admin, null,
            parent.banqueBob.id);
    transaction.is_clopes = true;
    parent.dernieresActions.add(transaction);
    transaction.WriteToDB(parent);
    Statement stmt = parent.connexion.createStatement();
    stmt.executeUpdate("UPDATE clopes SET quantite=quantite+" + quantite + " WHERE marque='"
        + marque + "'");
  }

  public void setPrix(int prix) throws Exception {
    Statement stmt = parent.connexion.createStatement();
    stmt.executeUpdate("UPDATE clopes SET prix=" + prix + " WHERE id='" + id + "'");
    this.prix = prix;
  }

  public static LinkedList<String> getMarques(MainWindow parent) throws Exception {
    Statement stmt = parent.connexion.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT marque FROM clopes ORDER BY quantite DESC");
    LinkedList<String> listeMarques = new LinkedList<String>();
    while (rs.next()) {
      listeMarques.add(rs.getString("marque"));
    }
    return listeMarques;
  }

  public void delete() throws Exception {
    Statement stmt = parent.connexion.createStatement();
    stmt.executeUpdate("DELETE FROM clopes WHERE marque='" + marque + "'");
  }

  public static void resetQuantites(MainWindow parent) throws Exception {
    Statement stmt = parent.connexion.createStatement();
    stmt.executeUpdate("UPDATE clopes SET quantite =0");
  }

  public static LinkedList<Clopes> getAllClopes(MainWindow parent) throws Exception {
    LinkedList<Clopes> res = new LinkedList<Clopes>();
    Statement stmt = parent.connexion.createStatement();
    ResultSet rs =
        stmt.executeQuery("SELECT marque FROM clopes ORDER BY quantite DESC, marque ASC");
    while (rs.next()) {
      Clopes clopes = new Clopes(parent, rs.getString("marque"));
      res.add(clopes);
    }
    return res;
  }
}
