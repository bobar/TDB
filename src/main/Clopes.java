package main;

import java.sql.ResultSet;
import java.sql.Statement;

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
	    prix = rs.getInt("prix");
	    quantite = rs.getInt("quantite");
	    if (rs.next()) { throw new TDBException("Clopes pas unique"); }
	} else {
	    throw new TDBException("Clopes inconnu : " + marque);
	}
    }

    public Clopes(MainWindow parent, String marque, int prix) throws Exception {
	this.parent = parent;
	this.marque = marque;
	this.prix = prix;
	this.id = 1;
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT max(id) as maxid FROM clopes");
	if (rs.next()) {
	    this.id = rs.getInt("maxid");
	}
	stmt.close();
    }

    public void Creer(Admin admin) throws Exception {
	Statement stmt = parent.connexion.createStatement();
	stmt.executeUpdate("INSERT INTO clopes (id,marque,prix,quantite) VALUES (" + id + ",'"
		+ marque + "'," + prix + ",0)");
	stmt.close();
	Transaction transaction =
		new Transaction(parent.banqueBob.id, 0, "Création clopes : " + marque, admin, null,
			parent.banqueBob.id);
	transaction.WriteToDB(parent);
    }
}
