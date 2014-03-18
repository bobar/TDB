package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

public class Droits {

	public static final String[] possibilites = { "log_eleve", "credit", "log_groupe", "transfert",
			"creer_tri", "modifier_tri", "supprimer_tri", "somme_tri", "voir_comptes",
			"debit_fichier", "export", "reinitialiser", "super_admin", "banque_binet",
			"gestion_clopes", "gestion_admin" };

	public static final String[] nom_droits = { "Log>20€", "Crediter", "Log groupé", "Transfert",
			"Créer un trigramme", "Modifier un trigramme", "Supprimer un trigramme",
			"Vérifier somme=0", "Voir des comptes par critère", "Débiter un fichier", "Exports",
			"Réinitialisation", "Mode super-administrateur", "Ouvrir/fermer un binet",
			"Gérer les clopes", "Gérer les admins" };

	private int permissions;
	private String nom;
	private Map<String, Boolean> droits;

	public MainWindow parent;

	public Droits(MainWindow parent, int permissions) throws SQLException, TrigException {
		this.parent = parent;
		this.permissions = permissions;
		this.droits = new HashMap<String, Boolean>();
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM droits WHERE permissions=" + permissions);
		if (rs.next()) {
			this.nom = rs.getString("nom");
			for (String type : possibilites) {
				this.droits.put(type, rs.getBoolean(type));
			}
		} else {
			throw new TrigException("Permissions inexistantes.");
		}
	}

	public Droits(MainWindow parent, String name) throws SQLException, TrigException {
		this.parent = parent;
		this.nom = name;
		this.droits = new HashMap<String, Boolean>();
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM droits WHERE nom='" + name + "'");
		if (rs.next()) {
			this.permissions = rs.getInt("permissions");
			for (String type : possibilites) {
				this.droits.put(type, rs.getBoolean(type));
			}
		} else {
			throw new TrigException("Permissions inexistantes.");
		}
	}

	public Droits(MainWindow parent, String nom, Vector<String> granted) throws SQLException {
		this.parent = parent;
		this.nom = nom;
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT max(permissions) as mp FROM droits");
		this.permissions = 0;
		if (rs.next()) {
			this.permissions = 1 + rs.getInt("mp");
		}
		this.droits = new HashMap<String, Boolean>();
		for (String droit : possibilites) {
			this.droits.put(droit, false);
		}
		for (String droit : granted) {
			this.droits.put(droit, true);
		}
	}

	public int permissions() {
		return this.permissions;
	}

	public Map<String, Boolean> droits() {
		return this.droits;
	}

	public String nom() {
		return this.nom;
	}

	public void updateDroits() throws SQLException {
		Statement stmt = parent.connexion.createStatement();
		String query = "UPDATE droits SET ";
		for (Map.Entry<String, Boolean> droit : droits.entrySet()) {
			query += droit.getKey() + "=" + droit.getValue() + ", ";
		}
		query += "nom='" + this.nom + "' WHERE permissions=" + this.permissions;
		stmt.executeUpdate(query);
	}

	public void delete() throws SQLException {
		Statement stmt = parent.connexion.createStatement();
		stmt.executeUpdate("DELETE FROM droits WHERE permissions=" + this.permissions);
	}

	public void creer() throws SQLException {
		Statement stmt = parent.connexion.createStatement();
		String query = "INSERT INTO droits (permissions, nom";
		for (String droit : possibilites) {
			query += ", " + droit;
		}
		query += ") VALUES (" + this.permissions + ", '" + this.nom + "'";
		for (String droit : possibilites) {
			query += ", " + this.droits.get(droit);
		}
		query += ")";
		stmt.executeUpdate(query);
	}

	public static Map<Integer, String> getStatuses(MainWindow parent) throws SQLException {
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT permissions,nom FROM droits");
		Map<Integer, String> res = new HashMap<Integer, String>();
		while (rs.next()) {
			res.put(rs.getInt("permissions"), rs.getString("nom"));
		}
		return res;
	}

	public static Map<String, Integer> getStatusId(MainWindow parent) throws SQLException {
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT permissions,nom FROM droits");
		Map<String, Integer> res = new HashMap<String, Integer>();
		while (rs.next()) {
			res.put(rs.getString("nom"), rs.getInt("permissions"));
		}
		return res;
	}

	public static LinkedList<Droits> getAllDroits(MainWindow parent) throws SQLException,
			TrigException {
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT permissions FROM droits");
		LinkedList<Droits> res = new LinkedList<Droits>();
		while (rs.next()) {
			Droits droit = new Droits(parent, rs.getInt("permissions"));
			res.add(droit);
		}
		return res;
	}

	public static boolean droitsUtilises(MainWindow parent, String nom) throws SQLException,
			DBException {
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs =
				stmt.executeQuery("SELECT count(*) as c FROM admins NATURAL JOIN droits WHERE nom='"
						+ nom + "'");
		if (rs.next()) {
			return rs.getInt("c") > 0;
		} else {
			throw new DBException("Droits inexistants");
		}
	}

	public static boolean existe(MainWindow parent, String nom) throws SQLException, DBException {
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs =
				stmt.executeQuery("SELECT count(*) as c FROM droits WHERE nom='" + nom + "'");
		if (rs.next()) {
			return rs.getInt("c") == 1;
		} else {
			throw new DBException("Droits inexistants");
		}
	}
}
