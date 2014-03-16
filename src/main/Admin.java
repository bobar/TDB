package main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

public class Admin {

    private static final int Pekin = 0;
    private static final int Ami = 1;
    private static final int ExBoBarman = 2;
    private static final int BoBarman = 3;
    public static final String[] status_array =
	    { "Pékin", "Ami du BôB", "Ex-BôBarman", "BôBarman" };
    public static final Vector<String> status = new Vector<String>(Arrays.asList(status_array));

    public int id;
    private int permissions;
    private String passwd;

    public MainWindow parent;

    public Admin(MainWindow parent, int id) throws Exception {
	this.parent = parent;
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM admins WHERE id=" + id);
	if (rs.next()) {
	    this.id = rs.getInt("id");
	    this.permissions = rs.getInt("permissions");
	    this.passwd = rs.getString("passwd");
	} else {
	    throw new TrigException("Aucun admin avec cet id");
	}
    }

    public Admin(MainWindow parent, String tri) throws Exception {
	this.parent = parent;
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs =
		stmt.executeQuery("SELECT id, permissions, passwd FROM admins NATURAL JOIN accounts WHERE trigramme='"
			+ tri + "'");
	if (rs.next()) {
	    this.id = rs.getInt("id");
	    this.permissions = rs.getInt("permissions");
	    this.passwd = rs.getString("passwd");
	} else {
	    throw new TrigException("Aucun administrateur avec ce trigramme.");
	}
    }

    public Admin(MainWindow parent, String tri, int permissions, String passwd) throws Exception {
	this.parent = parent;
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT id FROM accounts WHERE trigramme='" + tri + "'");
	if (rs.next()) {
	    this.id = rs.getInt("id");
	    this.permissions = permissions;
	    this.passwd = passwd;
	} else {
	    throw new TrigException("Trigramme inexistant");
	}
    }

    public Admin(MainWindow parent, String tri, String passwd) throws Exception {
	this.parent = parent;
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs =
		stmt.executeQuery("SELECT id,permissions,passwd FROM admins NATURAL JOIN accounts WHERE trigramme='"
			+ tri + "'");
	if (rs.next()) {
	    this.id = rs.getInt("id");
	    this.permissions = rs.getInt("permissions");
	    this.passwd = rs.getString("passwd");
	    if (!this.passwd.equals(passwd)) {
		throw new AuthException("Le mot de passe est incorrect.");
	    }
	} else {
	    throw new TrigException("Pas d'administrateur avec ce trigramme");
	}
    }

    public void creer() throws Exception {
	PreparedStatement stmt =
		parent.connexion
			.prepareStatement("INSERT INTO admins (id,permissions,passwd) VALUES (?,?,?)");
	stmt.setInt(1, this.id);
	stmt.setInt(2, this.permissions);
	stmt.setString(3, this.passwd);
	stmt.executeUpdate();
	Transaction transaction =
		new Transaction(id, 0, "Nommé administrateur : " + status.get(this.permissions),
			null, null, parent.banqueBob.id);
	transaction.WriteToDB(parent);
    }

    public void setPasswd(String MD5) throws Exception {
	this.passwd = MD5;
	Statement stmt = parent.connexion.createStatement();
	stmt.executeUpdate("UPDATE admins SET passwd='" + MD5 + "' WHERE id=" + this.id);
    }

    public void setPerms(int perms) throws Exception {
	this.permissions = perms;
	Statement stmt = parent.connexion.createStatement();
	stmt.executeUpdate("UPDATE admins SET permissions=" + perms + " WHERE id=" + this.id);
	Transaction transaction =
		new Transaction(id, 0, "Nouveau statut administrateur : " + status.get(perms),
			null, null, parent.banqueBob.id);
	transaction.WriteToDB(parent);
    }
    public String getStatus() {
	return Admin.status_array[this.permissions];
    }

    public void supprimer() throws Exception {
	Statement stmt = parent.connexion.createStatement();
	stmt.executeUpdate("DELETE FROM admins WHERE id=" + this.id);
	Transaction transaction =
		new Transaction(id, 0, "Viré des administrateurs, et bim !", null, null,
			parent.banqueBob.id);
	transaction.WriteToDB(parent);
    }

    public boolean BoBarman() {
	return this.permissions >= BoBarman;
    }

    public boolean exBoBarman() {
	return this.permissions >= ExBoBarman;
    }
    public boolean ami() {
	return this.permissions >= Ami;
    }
    public boolean pekin() {
	return this.permissions >= Pekin;
    }

    public static class AdminData {
	public String trigramme;
	public String name;
	public String firstname;
	public String permissions;
    }

    public static LinkedList<AdminData> getAllAdmins(MainWindow parent) throws Exception {
	LinkedList<AdminData> res = new LinkedList<AdminData>();
	Statement stmt = parent.connexion.createStatement();
	ResultSet rs =
		stmt.executeQuery("SELECT permissions,trigramme,name,first_name FROM admins NATURAL JOIN accounts ORDER BY permissions DESC,name ASC");
	while (rs.next()) {
	    AdminData admin = new AdminData();
	    admin.trigramme = rs.getString("trigramme");
	    admin.name = rs.getString("name");
	    admin.firstname = rs.getString("first_name");
	    admin.permissions = Admin.status_array[rs.getInt("permissions")];
	    res.add(admin);
	}
	return res;
    }

}
