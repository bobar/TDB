package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import admin.AuthentificationDialog;

public class Export {

    MainWindow parent;
    String chemin = "/home/thierry/Bureau/";

    public Export(MainWindow parent) {
	this.parent = parent;
    }

    public void positivation(int seuil, int promo) throws Exception {

	JFileChooser chooser = new JFileChooser();
	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	int returnVal = chooser.showOpenDialog(parent);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    chemin = chooser.getSelectedFile().getPath();

	    GregorianCalendar date = new GregorianCalendar();
	    date.setTime(new Date());
	    String jour = "", mois = "", annee = "", heure = "", minute = "";
	    if (date.get(Calendar.DAY_OF_MONTH) >= 10) {
		jour = "" + date.get(Calendar.DAY_OF_MONTH);
	    } else {
		jour = "0" + date.get(Calendar.DAY_OF_MONTH);
	    }
	    if ((1 + date.get(Calendar.MONTH)) >= 10) {
		mois = "" + (1 + date.get(Calendar.MONTH));
	    } else {
		mois = "0" + (1 + date.get(Calendar.MONTH));
	    }
	    if (date.get(Calendar.YEAR) >= 10) {
		annee = "" + date.get(Calendar.YEAR);
	    } else {
		annee = "0" + date.get(Calendar.YEAR);
	    }
	    if (date.get(Calendar.HOUR_OF_DAY) >= 10) {
		heure = "" + date.get(Calendar.HOUR_OF_DAY);
	    } else {
		heure = "0" + date.get(Calendar.HOUR);
	    }
	    if (date.get(Calendar.MINUTE) >= 10) {
		minute = "" + date.get(Calendar.MINUTE);
	    } else {
		minute = "0" + date.get(Calendar.MINUTE);
	    }
	    String dateComplete = annee + "-" + mois + "-" + jour + "_" + heure + "h" + minute;
	    String fichier = chemin + "/positivation_" + dateComplete + ".csv";
	    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fichier)));

	    PreparedStatement stmt = parent.connexion
		    .prepareStatement("SELECT trigramme,name,first_name,casert,balance FROM accounts WHERE status=0 AND promo= ? AND balance<= ? ORDER BY casert ASC");
	    stmt.setInt(1, promo);
	    stmt.setInt(2, seuil);
	    ResultSet rs = stmt.executeQuery();

	    while (rs.next()) {
		out.println(rs.getString("trigramme") + "," + rs.getString("name") + "," + rs.getString("first_name")
			+ "," + rs.getString("casert") + "," + ((double) rs.getInt("balance") / 100));
	    }
	    out.close();
	    JOptionPane.showConfirmDialog(parent, "Fichier sauvegardé dans " + chemin, "", JOptionPane.PLAIN_MESSAGE);
	}
    }

    public void exporterBase() throws Exception {

	AuthentificationDialog authentification = new AuthentificationDialog(this.parent);
	authentification.executer();

	if (authentification.droits == AuthentificationDialog.BoBarman) {
	    JFileChooser chooser = new JFileChooser();
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    int returnVal = chooser.showOpenDialog(parent);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		chemin = chooser.getSelectedFile().getPath();

		GregorianCalendar date = new GregorianCalendar();
		date.setTime(new Date());
		String jour = "", mois = "", annee = "", heure = "", minute = "";
		if (date.get(Calendar.DAY_OF_MONTH) >= 10) {
		    jour = "" + date.get(Calendar.DAY_OF_MONTH);
		} else {
		    jour = "0" + date.get(Calendar.DAY_OF_MONTH);
		}
		if ((1 + date.get(Calendar.MONTH)) >= 10) {
		    mois = "" + (1 + date.get(Calendar.MONTH));
		} else {
		    mois = "0" + (1 + date.get(Calendar.MONTH));
		}
		if (date.get(Calendar.YEAR) >= 10) {
		    annee = "" + date.get(Calendar.YEAR);
		} else {
		    annee = "0" + date.get(Calendar.YEAR);
		}
		if (date.get(Calendar.HOUR_OF_DAY) >= 10) {
		    heure = "" + date.get(Calendar.HOUR_OF_DAY);
		} else {
		    heure = "0" + date.get(Calendar.HOUR);
		}
		if (date.get(Calendar.MINUTE) >= 10) {
		    minute = "" + date.get(Calendar.MINUTE);
		} else {
		    minute = "0" + date.get(Calendar.MINUTE);
		}
		String dateComplete = annee + "-" + mois + "-" + jour + "_" + heure + "h" + minute;
		String fichier = chemin + "/transactions_" + dateComplete + ".csv";
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fichier)));

		PreparedStatement stmt = parent.connexion
			.prepareStatement("SELECT * FROM transactions ORDER BY date DESC");
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
		    out.println(rs.getInt("id") + "," + rs.getInt("price") + "," + rs.getString("comment") + ","
			    + rs.getInt("id") + "," + rs.getInt("date") + "," + rs.getInt("id2"));
		}
		out.close();

		fichier = chemin + "/accounts_" + dateComplete + ".csv";
		stmt = parent.connexion.prepareStatement("SELECT * FROM accounts ORDER BY id DESC");
		rs = stmt.executeQuery();
		out = new PrintWriter(new BufferedWriter(new FileWriter(fichier)));
		while (rs.next()) {
		    out.println(rs.getInt("id") + "," + rs.getString("trigramme") + "," + rs.getString("name") + ","
			    + rs.getString("first_name") + "," + rs.getString("nickname") + ","
			    + rs.getString("casert") + "," + rs.getInt("status") + "," + rs.getInt("promo")
			    + rs.getString("mail") + "," + rs.getString("picture") + "," + rs.getInt("balance") + ","
			    + rs.getString("turnover"));
		}
		out.close();

		fichier = chemin + "/admins_" + dateComplete + ".csv";
		stmt = parent.connexion.prepareStatement("SELECT * FROM admins ORDER BY id DESC");
		rs = stmt.executeQuery();
		out.close();
		out = new PrintWriter(new BufferedWriter(new FileWriter(fichier)));
		while (rs.next()) {
		    out.println(rs.getInt("id") + "," + rs.getInt("permissions") + "," + rs.getString("passwd"));
		}
		out.close();

		fichier = chemin + "/clopes_" + dateComplete + ".csv";
		stmt = parent.connexion.prepareStatement("SELECT * FROM clopes ORDER BY marque");
		rs = stmt.executeQuery();
		out = new PrintWriter(new BufferedWriter(new FileWriter(fichier)));
		while (rs.next()) {
		    out.println(rs.getString("marque") + "," + rs.getInt("prix") + "," + rs.getInt("quantite"));
		}
		out.close();

		JOptionPane.showConfirmDialog(parent, "Fichiers sauvegardés dans " + chemin, "",
			JOptionPane.PLAIN_MESSAGE);
	    }
	} else {
	    throw new TDBException("Vous n'avez pas les droits");
	}

    }

    public void sauverListe(JTable table) throws Exception {

	JFileChooser chooser = new JFileChooser();
	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	int returnVal = chooser.showOpenDialog(parent);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    chemin = chooser.getSelectedFile().getPath();
	    String titre = JOptionPane.showInputDialog(parent, "Saisir un titre", "", JOptionPane.QUESTION_MESSAGE);

	    TableModel modele = table.getModel();

	    GregorianCalendar date = new GregorianCalendar();
	    date.setTime(new Date());
	    String jour = "", mois = "", annee = "", heure = "", minute = "";
	    if (date.get(Calendar.DAY_OF_MONTH) >= 10) {
		jour = "" + date.get(Calendar.DAY_OF_MONTH);
	    } else {
		jour = "0" + date.get(Calendar.DAY_OF_MONTH);
	    }
	    if ((1 + date.get(Calendar.MONTH)) >= 10) {
		mois = "" + (1 + date.get(Calendar.MONTH));
	    } else {
		mois = "0" + (1 + date.get(Calendar.MONTH));
	    }
	    if (date.get(Calendar.YEAR) >= 10) {
		annee = "" + date.get(Calendar.YEAR);
	    } else {
		annee = "0" + date.get(Calendar.YEAR);
	    }
	    if (date.get(Calendar.HOUR_OF_DAY) >= 10) {
		heure = "" + date.get(Calendar.HOUR_OF_DAY);
	    } else {
		heure = "0" + date.get(Calendar.HOUR);
	    }
	    if (date.get(Calendar.MINUTE) >= 10) {
		minute = "" + date.get(Calendar.MINUTE);
	    } else {
		minute = "0" + date.get(Calendar.MINUTE);
	    }
	    String dateComplete = annee + "-" + mois + "-" + jour + "_" + heure + "h" + minute;
	    String fichier = chemin + "/" + titre + "_" + dateComplete + ".csv";
	    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fichier)));

	    int nbLignes = modele.getRowCount();
	    int nbColonnes = modele.getColumnCount();
	    for (int i = 0; i < nbLignes; i++) {
		for (int j = 0; j < nbColonnes; j++) {
		    if (j != nbColonnes - 1) {
			out.print(modele.getValueAt(i, j) + ",");
		    } else {
			out.println(modele.getValueAt(i, j).toString());
		    }
		}
	    }
	    out.close();
	}
    }
}
