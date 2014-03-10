package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;
import admin.AuthentificationDialog;

public class Trigramme {

    public static final int XPlatal = 0;
    public static final int XAncien = 1;
    public static final int Binet = 2;
    public static final int Personnel = 3;
    public static final int Etudiant = 4;
    public static final int Autre = 5;
    public static final String[] categoriesList = { "X Platalien", "X Ancien", "Binet",
	    "Personnel", "Etudiant non X", "Autre" };

    public MainWindow parent;
    public int id;
    public String trigramme;
    public String name;
    public String first_name;
    public String nickname;
    public String casert;
    public int status; // 0=X platal,1=X
		       // ancien,2=binet,3=personnel,4=supop,5=autre
    public int promo;
    public String mail;
    public String picture;
    public int balance;
    public int turnover;

    public Trigramme(MainWindow parent, String tri) throws Exception {
	this.parent = parent;
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE trigramme='" + tri + "'");
	if (rs.next()) {
	    id = rs.getInt("id");
	    trigramme = rs.getString("trigramme");
	    name = rs.getString("name");
	    first_name = rs.getString("first_name");
	    nickname = rs.getString("nickname");
	    casert = rs.getString("casert");
	    status = rs.getInt("status");
	    promo = rs.getInt("promo");
	    picture = rs.getString("picture");
	    balance = rs.getInt("balance");
	    turnover = rs.getInt("turnover");
	    if (rs.next()) { throw new TDBException("Trigramme pas unique"); }
	} else {
	    throw new TDBException("Trigramme inconnu : " + tri);
	}
    }

    public Trigramme(MainWindow parent, String trigramme, String name, String first_name,
	    String nickname, String casert, int status, int promo, String mail, String picture,
	    int balance, int turnover) {
	this.parent = parent;
	this.trigramme = trigramme;
	this.name = name;
	this.first_name = first_name;
	this.nickname = nickname;
	this.casert = casert;
	this.status = status;
	this.promo = promo;
	this.mail = mail;
	this.picture = picture;
	this.balance = balance;
	this.turnover = turnover;
    }

    public Trigramme(MainWindow parent, int id) throws Exception {
	this.parent = parent;
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE id=" + id);
	if (rs.next()) {
	    this.id = rs.getInt("id");
	    trigramme = rs.getString("trigramme");
	    name = rs.getString("name");
	    first_name = rs.getString("first_name");
	    nickname = rs.getString("nickname");
	    casert = rs.getString("casert");
	    status = rs.getInt("status");
	    promo = rs.getInt("promo");
	    picture = rs.getString("picture");
	    balance = rs.getInt("balance");
	    turnover = rs.getInt("turnover");
	}
    }

    public void creer(Admin admin) throws Exception {

	Statement stmt = parent.connexion.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT MAX(id) as maxid FROM accounts");
	int id = 0;
	if (rs.next()) {
	    id = rs.getInt("maxid") + 1;
	}
	ResultSet rs1 =
		stmt.executeQuery("SELECT id FROM accounts WHERE trigramme='" + trigramme + "'");
	if (rs1.first()) { throw new TDBException("Trigramme existant"); }
	PreparedStatement stmt2 =
		parent.connexion
			.prepareStatement("INSERT INTO accounts (id,trigramme, name, first_name, nickname, casert, status, promo, mail, picture, balance, turnover) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
	stmt2.setInt(1, id);
	stmt2.setString(2, trigramme);
	stmt2.setString(3, name);
	stmt2.setString(4, first_name);
	stmt2.setString(5, nickname);
	stmt2.setString(6, casert);
	stmt2.setInt(7, status);
	if (promo > 0) {
	    stmt2.setInt(8, promo);
	} else {
	    stmt2.setNull(8, Types.INTEGER);
	}
	stmt2.setString(9, mail);
	stmt2.setString(10, picture);
	stmt2.setInt(11, 0);
	stmt2.setInt(12, turnover);
	stmt2.executeUpdate();

	Transaction transaction =
		new Transaction(id, balance, "Création de trigramme", admin, null,
			parent.banqueBob.id);
	transaction.WriteToDB(parent.connexion);
	parent.setTrigrammeActif(new Trigramme(parent, trigramme));
    }

    public int getId() throws Exception {
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs =
		stmt.executeQuery("SELECT id FROM accounts WHERE trigramme='" + trigramme + "'");
	if (rs.next()) {
	    int id = rs.getInt("id");
	    if (rs.next()) {
		throw new TDBException("Trigramme double");
	    } else {
		return id;
	    }
	} else {
	    throw new TDBException("Trigramme inexistant");
	}
    }

    public void modifier(int id, Admin admin) throws Exception {
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT trigramme FROM accounts WHERE id=" + id);

	if (rs.first()) {
	    PreparedStatement stmt2 =
		    parent.connexion
			    .prepareStatement("UPDATE accounts SET trigramme = ?, name = ?, first_name = ?, nickname = ?, casert = ?, status = ?, promo = ?, picture = ? WHERE id=?");
	    stmt2.setString(1, trigramme);
	    stmt2.setString(2, name);
	    stmt2.setString(3, first_name);
	    stmt2.setString(4, nickname);
	    stmt2.setString(5, casert);
	    stmt2.setInt(6, status);
	    if (promo > 0) {
		stmt2.setInt(7, promo);
	    } else {
		stmt2.setNull(7, Types.INTEGER);
	    }
	    stmt2.setString(8, picture);
	    stmt2.setInt(9, id);
	    stmt2.executeUpdate();

	    Transaction transaction =
		    new Transaction(id, 0, "Modification du trigramme (ancien trigramme : "
			    + rs.getString("trigramme") + ")", admin, null, parent.banqueBob.id);
	    transaction.WriteToDB(parent.connexion);
	} else {
	    throw new TDBException("Trigramme inexistant");
	}
    }

    public void debiter(int montant) throws Exception {
	if (Math.abs(montant) > 1000000) { throw new TDBException(
		"Opération annulée car le montant est trop élevé"); }
	if (montant >= 0 && montant <= 2000) {
	    if (balance - montant < 0 && status != Trigramme.XPlatal) {
		AuthentificationDialog authentification = new AuthentificationDialog(parent);
		authentification.executer();
		if (!authentification.admin.ami()) { throw new TDBException(
			"Vous n'avez pas les droits de faire passer cette personne en négatif"); }
	    }
	    int banqueId = parent.banqueBob.id;
	    if (!parent.banqueBobActif) {
		banqueId = parent.banqueBinet.id;
	    }
	    Transaction transaction = new Transaction(id, -montant, "", null, null, banqueId);
	    parent.dernieresActions.add(transaction);
	    transaction.WriteToDB(parent.connexion);
	} else if (montant > 2000) {
	    AuthentificationDialog authentification = new AuthentificationDialog(parent);
	    authentification.executer();
	    if (authentification.admin.ami()) {
		int banqueId = parent.banqueBob.id;
		if (!parent.banqueBobActif) {
		    banqueId = parent.banqueBinet.id;
		}
		Transaction transaction =
			new Transaction(id, -montant, "", authentification.admin, null, banqueId);
		parent.dernieresActions.add(transaction);
		transaction.WriteToDB(parent.connexion);
	    } else {
		throw new TDBException("Vous n'avez pas les droits");
	    }
	} else {
	    AuthentificationDialog authentification = new AuthentificationDialog(parent);
	    authentification.executer();
	    if (authentification.admin.ami()) {
		Transaction transaction =
			new Transaction(id, montant, "", authentification.admin, null,
				parent.banqueBob.id);
		parent.dernieresActions.add(transaction);
		transaction.WriteToDB(parent.connexion);
	    } else {
		throw new TDBException("Vous n'avez pas les droits");
	    }
	}
	Thread.sleep(200);
	parent.trigrammeActif = new Trigramme(parent, parent.trigrammeActif.trigramme);
	parent.refresh();

    }

    public void crediter(Connection connexion, int montant, String commentaire, Admin admin)
	    throws Exception {
	if (Math.abs(montant) > 100000) { throw new TDBException(
		"Opération annulée car le montant est trop élevé"); }
	int banqueId = parent.banqueBob.id;

	Transaction transaction = new Transaction(id, montant, commentaire, admin, null, banqueId);
	transaction.WriteToDB(parent.connexion);
	parent.trigrammeActif = new Trigramme(parent, parent.trigrammeActif.trigramme);
	parent.refresh();
    }

    public void supprimer() throws Exception {
	AuthentificationDialog authentification = new AuthentificationDialog(parent);
	authentification.executer();
	if (authentification.admin.BoBarman()) {
	    if (parent.trigrammeActif.balance == 0) {
		Statement stmt = parent.connexion.createStatement();
		stmt.executeUpdate("DELETE FROM accounts WHERE id=" + id);
		stmt.executeUpdate("DELETE FROM admins WHERE id=" + id);
		stmt.executeUpdate("DELETE FROM transactions WHERE id=" + id);
		stmt.executeUpdate("DELETE FROM transactions WHERE id2=" + id);
		stmt.closeOnCompletion();
	    } else {
		throw new TDBException("Le trigramme doit être à 0.");
	    }
	} else {
	    throw new TDBException("Vous n'avez pas les droits.");
	}
    }

    public static boolean exists(MainWindow parent, String tri) throws Exception {
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs =
		stmt.executeQuery("SELECT count(*) as c FROM accounts WHERE trigramme='" + tri
			+ "'");
	if (rs.next()) {
	    return (rs.getInt("c") > 0);
	} else {
	    throw new TDBException("Impossible de chercher dans la base");
	}
    }

    public static Vector<Trigramme> rechercher(Connection connexion, String str) throws Exception {
	Statement stmt = connexion.createStatement();
	ResultSet rs =
		stmt.executeQuery("SELECT * FROM accounts WHERE name LIKE '%" + str
			+ "%' OR first_name LIKE '%" + str + "%' OR nickname LIKE '%" + str + "%'");
	Vector<Trigramme> res = new Vector<Trigramme>();
	while (rs.next()) {
	    Trigramme zou =
		    new Trigramme(null, rs.getString("trigramme"), rs.getString("name"),
			    rs.getString("first_name"), rs.getString("nickname"),
			    rs.getString("casert"), rs.getInt("status"), rs.getInt("promo"),
			    rs.getString("mail"), rs.getString("picture"), rs.getInt("balance"),
			    rs.getInt("turnover"));
	    res.add(zou);
	}
	return res;
    }
}
