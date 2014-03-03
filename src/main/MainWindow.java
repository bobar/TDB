package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Stack;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import operationsStandard.TrigrammeDialog;
import admin.AuthentificationDialog;

public class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    // Les variables globales

    public Trigramme trigrammeActif = null; // Le trigramme ouvert
    public Trigramme banqueBob = null; // Le trigramme banque du BôB
    public Trigramme banqueBinet = null; // Le trigramme banque binet
    public boolean banqueBobActif = true; // True: banque=BôB;False:
					  // banque=binet
    public boolean modeAdministrateur = false; // True: mode
					       // super-administrateur
    public Trigramme administrateur = null; // Le trig du super-admin
    public Connection connexion; // Pour la base de données, les autres
				 // fonctions en ont besoin
    public Stack<Transaction> dernieresActions;

    private MainWindowListener mainWindowListener = new MainWindowListener(this);
    Preferences prefs = Preferences.systemNodeForPackage(this.getClass());

    // Création de tous les menus, on les met en variable global pour y avoir
    // accès dans le listener

    private JMenu menuStandard = new JMenu("Opérations standard");
    JMenuItem ouvrirTrigramme = new JMenuItem("Ouvrir un trigramme");
    JMenuItem fermerTrigramme = new JMenuItem("Fermer le trigramme");
    JMenuItem voirHistorique = new JMenuItem("Voir tout l'historique");
    JMenuItem voirAncienHistorique = new JMenuItem("Voir ancien historique");
    JMenuItem rechercherTrigramme = new JMenuItem("Chercher un trigramme");
    JMenuItem debiterTrigramme = new JMenuItem("Débiter un trigramme");
    JMenuItem acheterClopes = new JMenuItem("Acheter des clopes");
    JMenuItem pinteDeKroPourSIE = new JMenuItem("Pinte de Kro pour SIE");
    JMenuItem annuler = new JMenuItem("Annuler");

    JMenu menuGestion = new JMenu("Gérer un trigramme");
    JMenuItem loggerAPlusieurs = new JMenuItem("Logger à plusieurs");
    JMenuItem approvisionner = new JMenuItem("Approvisionner le trigramme");
    JMenuItem transfert = new JMenuItem("Transfert");
    JMenuItem creerTrigramme = new JMenuItem("Créer un trigramme");
    JMenuItem modifierTrigramme = new JMenuItem("Modifier un trigramme");
    JMenuItem supprimerTrigramme = new JMenuItem("Supprimer un trigramme");

    JMenu menuTDB = new JMenu("Menu du TDB");
    JMenuItem verifTotal = new JMenuItem("Vérifier somme trigrammes");
    JMenuItem voirBinets = new JMenuItem("Voir les binets");
    JMenuItem voirComptes = new JMenuItem("Voir des comptes");
    JMenuItem debiterFichier = new JMenuItem("Débiter depuis un fichier");
    JMenuItem positivation = new JMenuItem("Positivation");
    JMenuItem exporter = new JMenuItem("Exporter la base");
    JMenuItem reinitialiserHistorique = new JMenuItem("Reinitialiser les historiques");
    JMenuItem reinitialiserConso = new JMenuItem("Reinitialiser les chiffres d'affaires");

    JMenu menuAdmin = new JMenu("Menu Administration");
    JMenuItem ouvrirModeAdmin = new JMenuItem("Lancer le mode super-administrateur");
    JMenuItem fermerModeAdmin = new JMenuItem("Fermer le mode super-administrateur");
    JMenuItem ouvrirBinet = new JMenuItem("Ouvrir banque binet");
    JMenuItem fermerBinet = new JMenuItem("Fermer banque binet");
    JMenuItem changerMDP = new JMenuItem("Changer son mot de passe");
    JMenuItem gestionAdmins = new JMenuItem("Gestion des comptes admin");
    JMenuItem gestionClopes = new JMenuItem("Gestion des clopes");

    JPanel fond;

    // Pour afficher l'historique
    JTable historique = new JTable() {
	private static final long serialVersionUID = 1L;

	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
	    Component c = super.prepareRenderer(renderer, row, column);
	    if ((row % 2) == 1) {
		c.setBackground(new Color((float) 0.9, (float) 0.9, (float) 0.9));
	    } else {
		c.setBackground(Color.WHITE);
	    }
	    return c;
	}
    };
    DefaultTableModel modele = new DefaultTableModel() {
	private static final long serialVersionUID = 1L;

	public boolean isCellEditable(int rowIndex, int columnInder) {
	    return false;
	}
    };
    /* DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() { private static final
     * long serialVersionUID = 1L;
     * 
     * public Component getTableCellRendererComponent(JTable table, Object value, boolean
     * isSelected, boolean hasFocus, int row, int column) { Component cell =
     * super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); if (row
     * % 2 == 0) { cell.setBackground(Color.red); } else { cell.setBackground(Color.white); } return
     * cell; } }; */
    DefaultTableColumnModel modeleColonnes;
    JScrollPane historiqueScrollPane = new JScrollPane();
    JPanel historiquePane = new JPanel();

    // Pour afficher le bordel à droite
    JPanel infos;
    JButton bobBanqueBouton;
    JButton binetBanqueBouton;
    JLabel trigrammeLabel;
    JTextPane nomLabel;
    JLabel balanceLabel;
    JLabel turnoverLabel;
    JLabel photo;

    public MainWindow() {}

    public void connecter() throws Exception {
	Database database = new Database();
	database.connecter();
	connexion = database.connexion;
    }

    public void deconnecter() throws Exception {
	this.connexion.close();
    }

    public void setTrigrammeActif(Trigramme trigramme) throws Exception {
	this.trigrammeActif = trigramme;
	dernieresActions = new Stack<Transaction>();
	this.refresh();
    }

    public void fermerTrigrammeActif() throws Exception {
	this.trigrammeActif = null;
	dernieresActions = new Stack<Transaction>();
	this.refresh();
    }

    public void ouvrirModeAdmin() throws Exception {
	AuthentificationDialog authentification = new AuthentificationDialog(this);
	authentification.executer();
	if (authentification.droits == AuthentificationDialog.BoBarman) {
	    modeAdministrateur = true;
	    administrateur = new Trigramme(this, authentification.admin);
	}
	this.refresh();
    }

    public void fermerModeAdmin() throws Exception {
	modeAdministrateur = false;
	administrateur = null;
	this.refresh();
    }

    public void ouvrirBanqueBinet() throws Exception {
	AuthentificationDialog authentification = new AuthentificationDialog(this);
	authentification.executer();
	if (authentification.droits >= AuthentificationDialog.ExBoBarman) {
	    if (trigrammeActif == null) {
		TrigrammeDialog dialog = new TrigrammeDialog(this, "");
		dialog.executer();
	    } else if (trigrammeActif.status != 2) {
		TrigrammeDialog dialog = new TrigrammeDialog(this, "");
		dialog.executer();

	    }
	    if (trigrammeActif.status != 2) { throw new TDBException(
		    "Le trigramme n'est pas un compte binet"); }
	    banqueBinet = trigrammeActif;
	    banqueBobActif = true;
	} else {
	    throw new TDBException("Vous n'avez pas les droits");
	}
	this.refresh();
    }

    public void fermerBanqueBinet() throws Exception {
	AuthentificationDialog authentification = new AuthentificationDialog(this);
	authentification.executer();
	if (authentification.droits >= AuthentificationDialog.ExBoBarman) {
	    banqueBinet = null;
	    banqueBobActif = true;
	} else {
	    throw new TDBException("Vous n'avez pas les droits");
	}
	this.refresh();
    }

    public void reinitialiserTurnover() throws Exception {
	AuthentificationDialog authentification = new AuthentificationDialog(this);
	authentification.executer();
	if (authentification.droits == AuthentificationDialog.BoBarman) {
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
	    String dateComplete = jour + "/" + mois + "/" + annee + " " + heure + ":" + minute;
	    prefs.put("dateResetTurnover", dateComplete);
	    Statement stmt = connexion.createStatement();
	    stmt.executeUpdate("UPDATE accounts SET turnover=+balance");
	    refresh();
	    JOptionPane.showMessageDialog(this, "Chiffres d'affaires réinitialisés", "",
		    JOptionPane.INFORMATION_MESSAGE);
	} else {
	    throw new TDBException("Vous n'avez pas les droits");
	}
    }

    public void reinitialiserHistorique() throws Exception {
	AuthentificationDialog authentification = new AuthentificationDialog(this);
	authentification.executer();
	if (authentification.droits == AuthentificationDialog.BoBarman) {
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
	    String dateComplete = jour + "/" + mois + "/" + annee + " " + heure + ":" + minute;
	    prefs.put("dateResetHistorique", dateComplete);
	    Statement stmt = connexion.createStatement();
	    stmt.executeUpdate("INSERT INTO transactions_history SELECT * FROM transactions");
	    stmt.executeUpdate("DELETE FROM transactions");
	    refresh();
	    JOptionPane.showMessageDialog(this, "Historiques réinitialisés", "",
		    JOptionPane.INFORMATION_MESSAGE);
	} else {
	    throw new TDBException("Vous n'avez pas les droits");
	}
    }

    public void annuler() throws Exception {
	if (!dernieresActions.empty()) {
	    Transaction transaction = dernieresActions.pop();
	    Statement stmt = connexion.createStatement();
	    stmt.executeUpdate("DELETE FROM transactions WHERE id=" + transaction.id
		    + " AND price=" + transaction.price + " AND admin=" + transaction.admin
		    + " AND date=" + transaction.date + " AND id2=" + transaction.id2);
	    if (transaction.price < 0) {
		Statement stmt2 = connexion.createStatement();
		stmt2.executeUpdate("UPDATE accounts SET balance=balance+" + (-transaction.price)
			+ " WHERE id=" + transaction.id);
		Statement stmt3 = connexion.createStatement();
		stmt3.executeUpdate("UPDATE accounts SET balance=balance-" + (-transaction.price)
			+ ",turnover=turnover-" + (-transaction.price) + " WHERE id="
			+ transaction.id2);

	    } else {
		Statement stmt2 = connexion.createStatement();
		stmt2.executeUpdate("UPDATE accounts SET balance=balance-" + (transaction.price)
			+ ",turnover=turnover-" + (transaction.price) + " WHERE id="
			+ transaction.id);
		Statement stmt3 = connexion.createStatement();
		stmt3.executeUpdate("UPDATE accounts SET balance=balance-" + (transaction.price)
			+ " WHERE id=" + transaction.id2);
	    }
	    this.refresh();
	}
    }

    public void supprimerTrigramme() throws Exception {
	if (trigrammeActif == null) {
	    TrigrammeDialog trigrammeDialog = new TrigrammeDialog(this, "");
	    TrigrammeDialog dialog = trigrammeDialog;
	    dialog.executer();
	}
	trigrammeActif.supprimer();
	trigrammeActif = null;
	this.refresh();
    }

    private String getExecutionPath() {
	String absolutePath =
		getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
	absolutePath = absolutePath.replaceAll("%20", " "); // Surely need to do this here
	if (absolutePath.substring(absolutePath.length() - 3).equals("bin")) {
	    absolutePath += "/.."; // Hack sordide pour l'éxécution dans Eclipse
	}
	return absolutePath;
    }

    public static void main(String[] args) {
	MainWindow TDB = new MainWindow();
	// TDB.prefs.put("version", "TDB 3.0");
	// TDB.prefs.put("auteur", "Thierry Deo");
	// TDB.prefs.put("dateMAJ", "25/01/2013");
	// TDB.prefs.put("dateResetTurnover", "25/01/2013 00:00");
	// TDB.prefs.put("dateResetHistorique", "25/01/2013 00:00");
	try {
	    // TDB.prefs.exportNode(System.out);
	    String trigrammeBanque = "BOB";
	    String absolutePath = TDB.getExecutionPath();
	    InputStream ips = new FileInputStream(absolutePath + "/src//TDB.config");
	    InputStreamReader ipsr = new InputStreamReader(ips);
	    BufferedReader br = new BufferedReader(ipsr);
	    String ligne;
	    while ((ligne = br.readLine()) != null) {
		if (ligne.charAt(0) != '#') {
		    int pos = ligne.indexOf('=');
		    String debut = ligne.substring(0, pos).trim();
		    String fin = ligne.substring(pos + 1, ligne.length()).trim();
		    if (debut.equals("trigrammeBanque") && fin.length() == 3)
			trigrammeBanque = fin;
		}
	    }
	    br.close();
	    TDB.initialiser(trigrammeBanque);
	} catch (Exception e) {
	    TDB.afficherErreur(e);
	}
    }

    public void afficherErreur(Exception e) {
	JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
	if (e.getClass() == SQLException.class) {
	    try {
		this.deconnecter();
		this.connecter();
	    } catch (Exception e1) {}
	}
	if (e.getClass() != TDBException.class) {
	    try {
		String absolutePath = this.getExecutionPath();
		PrintWriter out =
			new PrintWriter(new BufferedWriter(new FileWriter(
				absolutePath + "//logTDB", true)));
		out.println(e.getMessage());
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
		String dateComplete = jour + "/" + mois + "/" + annee + " " + heure + ":" + minute;
		out.println(dateComplete);
		out.println();
		for (StackTraceElement zou : e.getStackTrace()) {
		    out.println(zou);
		}
		out.println();
		out.close();
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}

    }

    public void afficherMythe() {
	JOptionPane.showMessageDialog(this,
		"Plap zde mythe" + "\nAuteur : " + prefs.get("auteur", "Thierry Deo")
			+ "\n\nReset chiffres d'affaires : " + prefs.get("dateResetTurnover", "")
			+ "\nReset historiques : " + prefs.get("dateResetHistorique", ""), "Mythe",
		JOptionPane.INFORMATION_MESSAGE);
	JOptionPane.showMessageDialog(this, "Manou Manou Manou Manou", "Mythe",
		JOptionPane.INFORMATION_MESSAGE);
    }

    public void initialiser(String trigrammeBanque) throws Exception {
	this.setTitle("TDB");
	Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
	tailleEcran.setSize(tailleEcran.getWidth() - 60, tailleEcran.getHeight());
	// hack sordide, a cause du lanceur Unity a gauche
	this.setSize(tailleEcran);
	// this.setSize(1280, 768);

	// Création de tous les menus
	ouvrirTrigramme.addActionListener(mainWindowListener);
	fermerTrigramme.addActionListener(mainWindowListener);
	fermerTrigramme.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_ESCAPE));
	voirHistorique.addActionListener(mainWindowListener);
	voirHistorique.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
		InputEvent.CTRL_DOWN_MASK));
	voirAncienHistorique.addActionListener(mainWindowListener);
	voirAncienHistorique.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
		InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
	rechercherTrigramme.addActionListener(mainWindowListener);
	rechercherTrigramme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
		InputEvent.CTRL_DOWN_MASK));
	debiterTrigramme.addActionListener(mainWindowListener);
	acheterClopes.addActionListener(mainWindowListener);
	acheterClopes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
		InputEvent.CTRL_DOWN_MASK));
	pinteDeKroPourSIE.addActionListener(mainWindowListener);
	pinteDeKroPourSIE.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
	annuler.addActionListener(mainWindowListener);
	annuler.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));

	menuStandard.add(ouvrirTrigramme);
	menuStandard.add(fermerTrigramme);
	menuStandard.add(voirHistorique);
	menuStandard.add(voirAncienHistorique);
	menuStandard.add(rechercherTrigramme);
	menuStandard.add(debiterTrigramme);
	menuStandard.add(acheterClopes);
	menuStandard.add(pinteDeKroPourSIE);
	menuStandard.add(annuler);

	loggerAPlusieurs.addActionListener(mainWindowListener);
	loggerAPlusieurs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
		InputEvent.CTRL_DOWN_MASK));
	approvisionner.addActionListener(mainWindowListener);
	approvisionner.setAccelerator(KeyStroke.getKeyStroke('+'));
	transfert.addActionListener(mainWindowListener);
	transfert.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
	creerTrigramme.addActionListener(mainWindowListener);
	creerTrigramme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
		InputEvent.CTRL_DOWN_MASK));
	modifierTrigramme.addActionListener(mainWindowListener);
	modifierTrigramme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
		InputEvent.CTRL_DOWN_MASK));
	supprimerTrigramme.addActionListener(mainWindowListener);
	supprimerTrigramme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
		InputEvent.CTRL_DOWN_MASK));

	menuGestion.add(loggerAPlusieurs);
	menuGestion.add(approvisionner);
	menuGestion.add(transfert);
	menuGestion.add(creerTrigramme);
	menuGestion.add(modifierTrigramme);
	menuGestion.add(supprimerTrigramme);

	verifTotal.addActionListener(mainWindowListener);
	voirBinets.addActionListener(mainWindowListener);
	voirComptes.addActionListener(mainWindowListener);
	debiterFichier.addActionListener(mainWindowListener);
	positivation.addActionListener(mainWindowListener);
	exporter.addActionListener(mainWindowListener);
	reinitialiserHistorique.addActionListener(mainWindowListener);
	reinitialiserConso.addActionListener(mainWindowListener);

	menuTDB.add(verifTotal);
	menuTDB.add(voirBinets);
	menuTDB.add(voirComptes);
	menuTDB.add(debiterFichier);
	menuTDB.add(positivation);
	menuTDB.add(exporter);
	menuTDB.add(reinitialiserHistorique);
	menuTDB.add(reinitialiserConso);

	ouvrirModeAdmin.addActionListener(mainWindowListener);
	fermerModeAdmin.addActionListener(mainWindowListener);
	ouvrirBinet.addActionListener(mainWindowListener);
	fermerBinet.addActionListener(mainWindowListener);
	changerMDP.addActionListener(mainWindowListener);
	gestionAdmins.addActionListener(mainWindowListener);
	gestionClopes.addActionListener(mainWindowListener);

	menuAdmin.add(ouvrirModeAdmin);
	menuAdmin.add(fermerModeAdmin);
	menuAdmin.add(ouvrirBinet);
	menuAdmin.add(fermerBinet);
	menuAdmin.add(changerMDP);
	menuAdmin.add(gestionAdmins);
	menuAdmin.add(gestionClopes);

	JMenuBar barreDeMenu = new JMenuBar();
	barreDeMenu.add(menuStandard);
	barreDeMenu.add(menuGestion);
	barreDeMenu.add(menuTDB);
	barreDeMenu.add(menuAdmin);

	this.setJMenuBar(barreDeMenu);

	// Création de l'historique

	historiqueScrollPane = new JScrollPane(historique);
	historiqueScrollPane
		.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	historiqueScrollPane.setPreferredSize(new Dimension((this.getWidth() - 20) * 2 / 3, this
		.getHeight() - 80));

	String[] header = { "Montant", "Banque", "Admin", "Commentaire", "Date" };
	modele.setColumnIdentifiers(header);

	historique.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	historique.setModel(modele);
	historique.getColumnModel().getColumn(0).setPreferredWidth(65);
	historique.getColumnModel().getColumn(1).setPreferredWidth(60);
	historique.getColumnModel().getColumn(2).setPreferredWidth(50);
	historique.getColumnModel().getColumn(3)
		.setPreferredWidth(historiqueScrollPane.getPreferredSize().width - 340);
	historique.getColumnModel().getColumn(4).setPreferredWidth(140);
	historique.setShowGrid(false);
	// historique.setDefaultRenderer(String.class, renderer);
	historique.repaint();

	// Création du panneau d'infos de droite
	infos = new JPanel();
	infos.setPreferredSize(new Dimension((this.getWidth() - 20) * 1 / 3, this.getHeight() - 80));
	infos.setLayout(new FlowLayout(SwingConstants.CENTER));
	infos.setBackground(null);

	if (trigrammeBanque.equals("BOB")) bobBanqueBouton = new JButton("BôB");
	else bobBanqueBouton = new JButton(trigrammeBanque);
	bobBanqueBouton.setFont(new Font("ARIAL", Font.BOLD, 32));
	bobBanqueBouton.setPreferredSize(new Dimension(
		(int) (infos.getPreferredSize().getWidth() - 20) / 2, 60));

	binetBanqueBouton = new JButton("Mythe");
	binetBanqueBouton.setFont(new Font("ARIAL", Font.BOLD, 32));
	binetBanqueBouton.setPreferredSize(new Dimension(
		(int) (infos.getPreferredSize().getWidth() - 20) / 2, 60));

	trigrammeLabel = new JLabel();
	trigrammeLabel.setPreferredSize(new Dimension(
		(int) (infos.getPreferredSize().getWidth() - 20), 60));
	trigrammeLabel.setHorizontalAlignment(SwingConstants.CENTER);
	trigrammeLabel.setFont(new Font("ARIAL", Font.BOLD, 40));

	nomLabel = new JTextPane();
	nomLabel.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 10),
		150));
	nomLabel.setOpaque(true);
	StyledDocument doc = nomLabel.getStyledDocument();
	MutableAttributeSet center = new SimpleAttributeSet();
	StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
	StyleConstants.setFontSize(center, 25);
	StyleConstants.setFontFamily(center, "ARIAL");
	doc.setParagraphAttributes(0, 0, center, true);
	nomLabel.setBackground(null);
	nomLabel.setEditable(false);
	nomLabel.setFocusable(false);

	balanceLabel = new JLabel();
	balanceLabel.setPreferredSize(new Dimension(
		(int) (infos.getPreferredSize().getWidth() - 10), 100));
	balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
	balanceLabel.setFont(new Font("ARIAL", Font.BOLD, 40));

	turnoverLabel = new JLabel();
	turnoverLabel.setPreferredSize(new Dimension(
		(int) (infos.getPreferredSize().getWidth() - 10), 40));
	turnoverLabel.setHorizontalAlignment(SwingConstants.CENTER);
	turnoverLabel.setFont(new Font("ARIAL", Font.PLAIN, 12));

	photo = new JLabel();
	photo.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 10), 200));
	photo.setHorizontalAlignment(SwingConstants.CENTER);

	infos.add(bobBanqueBouton);
	infos.add(binetBanqueBouton);
	infos.add(trigrammeLabel);
	infos.add(nomLabel);
	infos.add(balanceLabel);
	infos.add(turnoverLabel);
	infos.add(photo);

	fond = new JPanel();
	fond.add(historiqueScrollPane);
	fond.add(infos);
	this.add(fond);

	this.setVisible(true);

	this.addKeyListener(mainWindowListener);
	historique.addKeyListener(mainWindowListener);
	infos.addKeyListener(mainWindowListener);
	bobBanqueBouton.addActionListener(mainWindowListener);
	bobBanqueBouton.setFocusable(false);
	binetBanqueBouton.addActionListener(mainWindowListener);
	binetBanqueBouton.setFocusable(false);

	// Finalement,connexion à la base de données.
	this.connecter();
	banqueBob = new Trigramme(this, trigrammeBanque);
	if (banqueBob.status != 2) {
	    this.dispose();
	    throw new TDBException("La banque doit être un compte binet");
	}
	banqueBobActif = true;

    }

    public void refresh() throws Exception {
	if (banqueBinet != null) {
	    binetBanqueBouton.setText(banqueBinet.trigramme);
	} else {
	    binetBanqueBouton.setText("Mythe");
	}

	if (!banqueBobActif) {
	    fond.setBackground(Color.CYAN);
	    historiqueScrollPane.setBackground(Color.CYAN);
	    historique.setBackground(Color.CYAN);
	} else {
	    fond.setBackground(null);
	    historiqueScrollPane.setBackground(null);
	    historique.setBackground(Color.WHITE);
	}
	if (modeAdministrateur) {
	    fond.setBackground(Color.YELLOW);
	}

	if (trigrammeActif != null) {
	    trigrammeActif = new Trigramme(this, trigrammeActif.id);
	}

	// En gros, ca met à jour tout l'affichage
	for (int i = modele.getRowCount() - 1; i >= 0; i--) {
	    modele.removeRow(i);
	}

	if (trigrammeActif != null) {
	    if (trigrammeActif.balance >= 0) {
		balanceLabel.setForeground(Color.BLACK);
	    } else {
		balanceLabel.setForeground(Color.RED);
	    }
	    if (trigrammeActif.status == Trigramme.XPlatal) {
		trigrammeLabel.setForeground(Color.BLACK);
	    } else if (trigrammeActif.status == Trigramme.Binet) {
		trigrammeLabel.setForeground(Color.GREEN);
	    } else {
		trigrammeLabel.setForeground(Color.BLUE);
	    }
	    Statement stmt = connexion.createStatement();
	    /* ResultSet rs = stmt .executeQuery(
	     * "SELECT price, id, id2, comment, admin, date FROM transactions WHERE transactions.id="
	     * + trigrammeActif.id +
	     * " UNION SELECT -price as price, id, id2, comment, admin, date FROM transactions WHERE transactions.id2="
	     * + trigrammeActif.id + " ORDER BY date DESC LIMIT 50"); */
	    ResultSet rs =
		    stmt.executeQuery("SELECT price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			    + " FROM transactions as tr "
			    + " JOIN accounts as ac1 ON ac1.id=tr.id "
			    + " JOIN accounts as ac2 ON ac2.id=tr.id2 "
			    + " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin "
			    + " WHERE tr.id="
			    + trigrammeActif.id
			    + " UNION SELECT -price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			    + " FROM transactions as tr "
			    + " JOIN accounts as ac1 ON ac1.id=tr.id2 "
			    + " JOIN accounts as ac2 ON ac2.id=tr.id "
			    + " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin "
			    + " WHERE tr.id2="
			    + trigrammeActif.id + " ORDER BY date DESC LIMIT 50");
	    while (rs.next()) {
		/* int adminId = rs.getInt("admin"); String adminTrig = ""; Statement stmt2 =
		 * connexion.createStatement(); ResultSet rs2 =
		 * stmt2.executeQuery("SELECT trigramme FROM accounts WHERE id=" + adminId); if
		 * (rs2.next()) { adminTrig = rs2.getString("trigramme"); } String banqueTrig = "";
		 * Statement stmt3 = connexion.createStatement(); ResultSet rs3 =
		 * stmt3.executeQuery("SELECT trigramme FROM accounts WHERE id=" + (rs.getInt("id")
		 * + rs.getInt("id2") - trigrammeActif.id)); if (rs3.next()) { banqueTrig =
		 * rs3.getString("trigramme"); } if (banqueTrig.equals("BOB")) { banqueTrig = ""; //
		 * Plus de lisibilité } */
		String adminTrig = rs.getString("t3");
		String banqueTrig = rs.getString("t2");
		if (banqueTrig.equals("BOB")) {
		    banqueTrig = "";
		}
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(((long) rs.getInt("date")) * 1000);
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
		String dateComplete = jour + "/" + mois + "/" + annee + " " + heure + ":" + minute;
		String[] item =
			{ ((double) rs.getInt("p") / 100) + "", banqueTrig, adminTrig,
				rs.getString("c"), dateComplete };
		modele.addRow(item);
	    }

	    trigrammeLabel.setText(trigrammeActif.trigramme);
	    String promo = "";
	    if (trigrammeActif.promo > 0) {
		promo = "" + trigrammeActif.promo;
	    }
	    if (!trigrammeActif.nickname.equals("")) {
		nomLabel.setText(trigrammeActif.name + " " + trigrammeActif.first_name + " ("
			+ trigrammeActif.nickname + ") " + promo);
	    } else {
		nomLabel.setText(trigrammeActif.name + " " + trigrammeActif.first_name + " "
			+ promo);
	    }
	    balanceLabel.setText("" + (double) trigrammeActif.balance / 100);
	    if (trigrammeActif.status == 2) {
		turnoverLabel
			.setText(((double) (trigrammeActif.balance - trigrammeActif.turnover) / 100)
				+ "€ gagnés depuis dernier reset.");
	    } else {
		turnoverLabel
			.setText(((double) (trigrammeActif.turnover - trigrammeActif.balance) / 100)
				+ "€ depensés depuis dernier reset.");
	    }
	    if (trigrammeActif.picture != "") {
		try {
		    Image image = ImageIO.read(new File(trigrammeActif.picture));
		    new ImageIcon(image);
		    double zoom =
			    Math.min((double) photo.getWidth() / (double) image.getWidth(null),
				    (double) photo.getHeight() / (double) image.getHeight(null));
		    photo.setIcon(new ImageIcon(image.getScaledInstance(
			    (int) (image.getWidth(null) * zoom),
			    (int) (image.getHeight(null) * zoom), Image.SCALE_DEFAULT)));
		    photo.repaint();
		} catch (IOException e) {
		    photo.setIcon(null);
		}
	    } else {
		photo.setIcon(null);
	    }

	} else {
	    trigrammeLabel.setText("");
	    nomLabel.setText("");
	    balanceLabel.setText("");
	    turnoverLabel.setText("");
	    photo.setIcon(null);
	}
	infos.repaint();
	// historique.setDefaultRenderer(String.class, renderer);
	historique.setModel(modele);
	historique.repaint();
	this.repaint();
    }
    public void refreshHistorique() throws Exception {

	// En gros, ca met à jour l'historique au complet (dans la précédente,
	// on affiche que 50 lignes)
	for (int i = modele.getRowCount() - 1; i >= 0; i--) {
	    modele.removeRow(i);
	}

	if (trigrammeActif != null) {
	    Statement stmt = connexion.createStatement();
	    /* ResultSet rs = stmt.executeQuery(
	     * "SELECT price, id, id2, comment, admin, date FROM transactions WHERE transactions.id="
	     * + trigrammeActif.id +
	     * " UNION SELECT -price as price, id, id2, comment, admin, date FROM transactions WHERE transactions.id2="
	     * + trigrammeActif.id + " ORDER BY date DESC"); */
	    ResultSet rs =
		    stmt.executeQuery("SELECT price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			    + " FROM transactions as tr "
			    + " JOIN accounts as ac1 ON ac1.id=tr.id "
			    + " JOIN accounts as ac2 ON ac2.id=tr.id2 "
			    + " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin "
			    + " WHERE tr.id="
			    + trigrammeActif.id
			    + " UNION SELECT -price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			    + " FROM transactions as tr "
			    + " JOIN accounts as ac1 ON ac1.id=tr.id2 "
			    + " JOIN accounts as ac2 ON ac2.id=tr.id "
			    + " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin "
			    + " WHERE tr.id2="
			    + trigrammeActif.id + " ORDER BY date DESC");
	    while (rs.next()) {
		/* int adminId = rs.getInt("admin"); String adminTrig = ""; Statement stmt2 =
		 * connexion.createStatement(); ResultSet rs2 =
		 * stmt2.executeQuery("SELECT trigramme FROM accounts WHERE id=" + adminId); if
		 * (rs2.next()) { adminTrig = rs2.getString("trigramme"); } String banqueTrig = "";
		 * Statement stmt3 = connexion.createStatement(); ResultSet rs3 =
		 * stmt3.executeQuery("SELECT trigramme FROM accounts WHERE id=" + (rs.getInt("id")
		 * + rs.getInt("id2") - trigrammeActif.id)); if (rs3.next()) { banqueTrig =
		 * rs3.getString("trigramme"); } if (banqueTrig.equals("BOB")) { banqueTrig = ""; //
		 * Plus de lisibilité } */
		String adminTrig = rs.getString("t3");
		String banqueTrig = rs.getString("t2");
		if (banqueTrig.equals("BOB")) {
		    banqueTrig = "";
		}
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(((long) rs.getInt("date")) * 1000);
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
		String dateComplete = jour + "/" + mois + "/" + annee + " " + heure + ":" + minute;
		String[] item =
			{ ((double) rs.getInt("p") / 100) + "", banqueTrig, adminTrig,
				rs.getString("c"), dateComplete };
		modele.addRow(item);
	    }
	    infos.repaint();
	    historique.setModel(modele);
	    historique.repaint();
	    this.repaint();
	}
    }
    public void refreshOldHistorique() throws Exception {

	// Cette fois, ca affiche l'historique de l'historique et l'historique
	for (int i = modele.getRowCount() - 1; i >= 0; i--) {
	    modele.removeRow(i);
	}

	if (trigrammeActif != null) {
	    Statement stmt = connexion.createStatement();
	    /* ResultSet rs = stmt.executeQuery(
	     * "SELECT price, id, id2, comment, admin, date FROM transactions WHERE transactions.id="
	     * + trigrammeActif.id +
	     * " UNION SELECT -price as price, id, id2, comment, admin, date FROM transactions WHERE transactions.id2="
	     * + trigrammeActif.id + " ORDER BY date DESC"); */
	    ResultSet rs =
		    stmt.executeQuery("SELECT price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			    + " FROM transactions as tr "
			    + " JOIN accounts as ac1 ON ac1.id=tr.id "
			    + " JOIN accounts as ac2 ON ac2.id=tr.id2 "
			    + " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin " + " WHERE tr.id="
			    + trigrammeActif.id
			    + " UNION SELECT -price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			    + " FROM transactions as tr "
			    + " JOIN accounts as ac1 ON ac1.id=tr.id2 "
			    + " JOIN accounts as ac2 ON ac2.id=tr.id "
			    + " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin "
			    + " WHERE tr.id2="
			    + trigrammeActif.id
			    + " UNION SELECT price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			    + " FROM transactions_history as tr "
			    + " JOIN accounts as ac1 ON ac1.id=tr.id "
			    + " JOIN accounts as ac2 ON ac2.id=tr.id2 "
			    + " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin "
			    + " WHERE tr.id="
			    + trigrammeActif.id
			    + " UNION SELECT -price as p,comment as c, ac1.trigramme as t1,ac2.trigramme as t2,ac3.trigramme as t3,date "
			    + " FROM transactions_history as tr "
			    + " JOIN accounts as ac1 ON ac1.id=tr.id2 "
			    + " JOIN accounts as ac2 ON ac2.id=tr.id "
			    + " LEFT JOIN accounts as ac3 ON ac3.id=tr.admin "
			    + " WHERE tr.id2="
			    + trigrammeActif.id + " ORDER BY date DESC");
	    while (rs.next()) {
		/* int adminId = rs.getInt("admin"); String adminTrig = ""; Statement stmt2 =
		 * connexion.createStatement(); ResultSet rs2 =
		 * stmt2.executeQuery("SELECT trigramme FROM accounts WHERE id=" + adminId); if
		 * (rs2.next()) { adminTrig = rs2.getString("trigramme"); } String banqueTrig = "";
		 * Statement stmt3 = connexion.createStatement(); ResultSet rs3 =
		 * stmt3.executeQuery("SELECT trigramme FROM accounts WHERE id=" + (rs.getInt("id")
		 * + rs.getInt("id2") - trigrammeActif.id)); if (rs3.next()) { banqueTrig =
		 * rs3.getString("trigramme"); } if (banqueTrig.equals("BOB")) { banqueTrig = ""; //
		 * Plus de lisibilité } */
		String adminTrig = rs.getString("t3");
		String banqueTrig = rs.getString("t2");
		if (banqueTrig.equals("BOB")) {
		    banqueTrig = "";
		}
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(((long) rs.getInt("date")) * 1000);
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
		String dateComplete = jour + "/" + mois + "/" + annee + " " + heure + ":" + minute;
		String[] item =
			{ ((double) rs.getInt("p") / 100) + "", banqueTrig, adminTrig,
				rs.getString("c"), dateComplete };
		modele.addRow(item);
	    }
	    infos.repaint();
	    historique.setModel(modele);
	    historique.repaint();
	    this.repaint();
	}
    }
}
