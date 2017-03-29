package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Stack;
import java.util.TimeZone;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
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
  public boolean banqueBobActif = true; // True : banque=BôB
  // False : banque=binet
  public boolean modeAdministrateur = false; // True: mode
  // super-administrateur
  public Admin administrateur = null; // Le trig du super-admin
  public Connection connexion; // Pour la base de données, les autres
  // fonctions en ont besoin
  public Stack<Transaction> dernieresActions;

  private MainWindowListener mainWindowListener = new MainWindowListener(this);
  Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

  public Properties properties = new Properties();

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
  JMenuItem fasciserParMail = new JMenuItem("Envoyer mail de fascisation");
  JMenuItem loggerAPlusieurs = new JMenuItem("Logger à plusieurs");
  JMenuItem approvisionner = new JMenuItem("Approvisionner le trigramme");
  JMenuItem transfert = new JMenuItem("Transfert");
  JMenuItem modifierMail = new JMenuItem("Définir l'adresse mail");
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
  JMenuItem gestionClopes = new JMenuItem("Gestion des clopes");
  JMenuItem gestionAdmins = new JMenuItem("Gestion des admins");
  JMenuItem gestionDroits = new JMenuItem("Gestion des droits");

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
  DefaultTableColumnModel modeleColonnes;
  JScrollPane historiqueScrollPane = new JScrollPane();
  JPanel historiquePane = new JPanel();

  // Pour afficher le bordel à droite
  JPanel infos;
  JButton bobBanqueBouton;
  JButton binetBanqueBouton;
  JLabel trigrammeLabel;
  JTextPane nomLabel;
  JLabel ageLabel;
  JLabel balanceLabel;
  JLabel turnoverLabel;
  JLabel photo;

  public MainWindow() {}

  public void connecter() throws Exception {
    Database database = new Database();
    database.connecter(properties);
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
    if (!authentification.admin.has_droit("super_admin")) {
      throw new AuthException();
    }
    modeAdministrateur = true;
    administrateur = authentification.admin;
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
    if (!authentification.admin.has_droit("banque_binet")) {
      throw new AuthException();
    }
    if (trigrammeActif == null || trigrammeActif.status != Trigramme.Binet) {
      TrigrammeDialog dialog = new TrigrammeDialog(this, "");
      dialog.executer();
    }
    if (trigrammeActif.status != Trigramme.Binet) {
      throw new TrigException("Le trigramme n'est pas un compte binet");
    }
    banqueBinet = trigrammeActif;
    banqueBobActif = true;
    this.refresh();
  }

  public void fermerBanqueBinet() throws Exception {
    AuthentificationDialog authentification = new AuthentificationDialog(this);
    authentification.executer();
    if (!authentification.admin.has_droit("banque_binet")) {
      throw new AuthException();
    }
    banqueBinet = null;
    banqueBobActif = true;
    this.refresh();
  }

  public void reinitialiserTurnover() throws Exception {
    AuthentificationDialog authentification = new AuthentificationDialog(this);
    authentification.executer();
    if (!authentification.admin.has_droit("reinitialiser")) {
      throw new AuthException();
    }
    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String date = formater.format(new Date());
    prefs.put("dateResetTurnover", date);
    Statement stmt = connexion.createStatement();
    stmt.executeUpdate("UPDATE accounts SET turnover=balance");
    stmt.executeUpdate("UPDATE accounts SET turnover=0 WHERE status=2");
    refresh();
    JOptionPane.showMessageDialog(this, "Chiffres d'affaires réinitialisés", "", JOptionPane.INFORMATION_MESSAGE);
  }

  public void reinitialiserHistorique() throws Exception {
    AuthentificationDialog authentification = new AuthentificationDialog(this);
    authentification.executer();
    if (!authentification.admin.has_droit("reinitialiser")) {
      throw new AuthException();
    }
    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String date = formater.format(new Date());
    prefs.put("dateResetHistorique", date);
    Statement stmt = connexion.createStatement();
    stmt.executeUpdate("INSERT INTO transactions_history SELECT * FROM transactions");
    stmt.executeUpdate("DELETE FROM transactions");
    refresh();
    JOptionPane.showMessageDialog(this, "Historiques réinitialisés", "", JOptionPane.INFORMATION_MESSAGE);
  }

  public void annuler() throws Exception {
    if (!dernieresActions.empty()) {
      Transaction transaction = dernieresActions.pop();
      transaction.annuler(connexion);
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
    String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
    absolutePath = absolutePath.replaceAll("%20", " "); // Surely need to do
    // this here
    if (absolutePath.substring(absolutePath.length() - 3).equals("bin")) {
      absolutePath += "/.."; // Hack sordide pour l'éxécution dans Eclipse
    }
    return absolutePath;
  }

  public static void main(String[] args) throws AddressException, MessagingException {
    MainWindow TDB = new MainWindow();
    try {
      String absolutePath = TDB.getExecutionPath();
      InputStream ips = new FileInputStream(absolutePath + "/src/TDB.config");
      Properties properties = new Properties();
      properties.load(ips);
      TDB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      TDB.initialiser(properties);
    } catch (Exception e) {
      TDB.afficherErreur(e);
    }
  }

  public void afficherErreur(Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    if (e.getClass() == SQLException.class) {
      try {
        this.deconnecter();
        this.connecter();
      } catch (Exception e1) {
      }
      return;
    }
    if (e.getClass() == NumberFormatException.class)
      return;
    if (!TDBException.class.isAssignableFrom(e.getClass())) {
      try {
        String absolutePath = this.getExecutionPath();
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(absolutePath + "//logTDB", true)));
        out.println(e.getMessage());
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formater.format(new Date());
        out.println(date);
        out.println(e.getStackTrace().toString().replaceAll("\n", "\t\t"));
        out.println("\n");
        out.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
  }

  public void afficherMythe() {
    JOptionPane.showMessageDialog(this,
        "Plap zdé mythe" + "\n\nReset chiffres d'affaires : " + prefs.get("dateResetTurnover", "")
            + "\nReset historiques : " + prefs.get("dateResetHistorique", ""),
        "Mythe", JOptionPane.INFORMATION_MESSAGE);
    JOptionPane.showMessageDialog(this, "Manou Manou Manou Manou", "Mythe", JOptionPane.INFORMATION_MESSAGE);
  }

  public void initialiser(Properties properties) throws Exception {
    this.properties = properties;
    this.setTitle("TDB");
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    int width = gd.getDisplayMode().getWidth();
    int height = gd.getDisplayMode().getHeight();
    Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
    Dimension tailleEcran = new Dimension();
    tailleEcran.setSize(width - scnMax.left, height - scnMax.bottom);
    this.setSize(tailleEcran);

    // Création de tous les menus
    if (!properties.getProperty("ouvrirTrigramme", "true").equals("false")) {
      ouvrirTrigramme.addActionListener(mainWindowListener);
      menuStandard.add(ouvrirTrigramme);
    }
    if (!properties.getProperty("fermerTrigramme", "true").equals("false")) {
      fermerTrigramme.addActionListener(mainWindowListener);
      fermerTrigramme.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_ESCAPE));
      menuStandard.add(fermerTrigramme);
    }
    if (!properties.getProperty("voirHistorique", "true").equals("false")) {
      voirHistorique.addActionListener(mainWindowListener);
      voirHistorique.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
      menuStandard.add(voirHistorique);
    }
    if (!properties.getProperty("voirAncienHistorique", "true").equals("false")) {
      voirAncienHistorique.addActionListener(mainWindowListener);
      voirAncienHistorique.setAccelerator(
          KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
      menuStandard.add(voirAncienHistorique);
    }
    if (!properties.getProperty("rechercherTrigramme", "true").equals("false")) {
      rechercherTrigramme.addActionListener(mainWindowListener);
      rechercherTrigramme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
      menuStandard.add(rechercherTrigramme);
    }
    if (!properties.getProperty("debiterTrigramme", "true").equals("false")) {
      debiterTrigramme.addActionListener(mainWindowListener);
      menuStandard.add(debiterTrigramme);
    }
    if (!properties.getProperty("acheterClopes", "true").equals("false")) {
      acheterClopes.addActionListener(mainWindowListener);
      acheterClopes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
      menuStandard.add(acheterClopes);
    }
    if (!properties.getProperty("pinteDeKroPourSIE", "true").equals("false")) {
      pinteDeKroPourSIE.addActionListener(mainWindowListener);
      pinteDeKroPourSIE
          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
      menuStandard.add(pinteDeKroPourSIE);
    }
    if (!properties.getProperty("annuler", "true").equals("false")) {
      annuler.addActionListener(mainWindowListener);
      annuler.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
      menuStandard.add(annuler);
    }

    if (!properties.getProperty("fasciserParMail", "true").equals("false")) {
      fasciserParMail.addActionListener(mainWindowListener);
      fasciserParMail
          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
      menuGestion.add(fasciserParMail);
    }
    if (!properties.getProperty("loggerAPlusieurs", "true").equals("false")) {
      loggerAPlusieurs.addActionListener(mainWindowListener);
      loggerAPlusieurs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
      menuGestion.add(loggerAPlusieurs);
    }
    if (!properties.getProperty("approvisionner", "true").equals("false")) {
      approvisionner.addActionListener(mainWindowListener);
      approvisionner.setAccelerator(KeyStroke.getKeyStroke('+'));
      menuGestion.add(approvisionner);
    }
    if (!properties.getProperty("transfert", "true").equals("false")) {
      transfert.addActionListener(mainWindowListener);
      transfert.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
      menuGestion.add(transfert);
    }
    if (!properties.getProperty("modifierMail", "true").equals("false")) {
      modifierMail.addActionListener(mainWindowListener);
      menuGestion.add(modifierMail);
    }
    if (!properties.getProperty("creerTrigramme", "true").equals("false")) {
      creerTrigramme.addActionListener(mainWindowListener);
      creerTrigramme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
      menuGestion.add(creerTrigramme);
    }
    if (!properties.getProperty("modifierTrigramme", "true").equals("false")) {
      modifierTrigramme.addActionListener(mainWindowListener);
      modifierTrigramme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
      menuGestion.add(modifierTrigramme);
    }
    if (!properties.getProperty("supprimerTrigramme", "true").equals("false")) {
      supprimerTrigramme.addActionListener(mainWindowListener);
      supprimerTrigramme.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
      menuGestion.add(supprimerTrigramme);
    }

    if (!properties.getProperty("verifTotal", "true").equals("false")) {
      verifTotal.addActionListener(mainWindowListener);
      menuTDB.add(verifTotal);
    }
    if (!properties.getProperty("voirBinets", "true").equals("false")) {
      voirBinets.addActionListener(mainWindowListener);
      menuTDB.add(voirBinets);
    }
    if (!properties.getProperty("voirComptes", "true").equals("false")) {
      voirComptes.addActionListener(mainWindowListener);
      menuTDB.add(voirComptes);
    }
    if (!properties.getProperty("debiterFichier", "true").equals("false")) {
      debiterFichier.addActionListener(mainWindowListener);
      menuTDB.add(debiterFichier);
    }
    if (!properties.getProperty("positivation", "true").equals("false")) {
      positivation.addActionListener(mainWindowListener);
      menuTDB.add(positivation);
    }
    if (!properties.getProperty("exporter", "true").equals("false")) {
      exporter.addActionListener(mainWindowListener);
      menuTDB.add(exporter);
    }
    if (!properties.getProperty("reinitialiserHistorique", "true").equals("false")) {
      reinitialiserHistorique.addActionListener(mainWindowListener);
      menuTDB.add(reinitialiserHistorique);
    }
    if (!properties.getProperty("reinitialiserConso", "true").equals("false")) {
      reinitialiserConso.addActionListener(mainWindowListener);
      menuTDB.add(reinitialiserConso);
    }

    if (!properties.getProperty("ouvrirModeAdmin", "true").equals("false")) {
      ouvrirModeAdmin.addActionListener(mainWindowListener);
      menuAdmin.add(ouvrirModeAdmin);
    }
    if (!properties.getProperty("fermerModeAdmin", "true").equals("false")) {
      fermerModeAdmin.addActionListener(mainWindowListener);
      menuAdmin.add(fermerModeAdmin);
    }
    if (!properties.getProperty("ouvrirBinet", "true").equals("false")) {
      ouvrirBinet.addActionListener(mainWindowListener);
      menuAdmin.add(ouvrirBinet);
    }
    if (!properties.getProperty("fermerBinet", "true").equals("false")) {
      fermerBinet.addActionListener(mainWindowListener);
      menuAdmin.add(fermerBinet);
    }
    if (!properties.getProperty("changerMDP", "true").equals("false")) {
      changerMDP.addActionListener(mainWindowListener);
      menuAdmin.add(changerMDP);
    }
    if (!properties.getProperty("gestionClopes", "true").equals("false")) {
      gestionClopes.addActionListener(mainWindowListener);
      menuAdmin.add(gestionClopes);
    }
    if (!properties.getProperty("gestionAdmins", "true").equals("false")) {
      gestionAdmins.addActionListener(mainWindowListener);
      menuAdmin.add(gestionAdmins);
    }
    if (!properties.getProperty("gestionDroits", "true").equals("false")) {
      gestionDroits.addActionListener(mainWindowListener);
      menuAdmin.add(gestionDroits);
    }

    JMenuBar barreDeMenu = new JMenuBar();
    barreDeMenu.add(menuStandard);
    barreDeMenu.add(menuGestion);
    barreDeMenu.add(menuTDB);
    barreDeMenu.add(menuAdmin);

    this.setJMenuBar(barreDeMenu);

    // Création de l'historique

    historiqueScrollPane = new JScrollPane(historique);
    historiqueScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    historiqueScrollPane.setPreferredSize(new Dimension((this.getWidth() - 20) * 2 / 3, this.getHeight() - 80));

    String[] header = {"Montant", "Banque", "Admin", "Commentaire", "Date"};
    modele.setColumnIdentifiers(header);

    historique.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    historique.setModel(modele);
    historique.getColumnModel().getColumn(0).setPreferredWidth(65);
    historique.getColumnModel().getColumn(1).setPreferredWidth(60);
    historique.getColumnModel().getColumn(2).setPreferredWidth(50);
    historique.getColumnModel().getColumn(3).setPreferredWidth(historiqueScrollPane.getPreferredSize().width - 340);
    historique.getColumnModel().getColumn(4).setPreferredWidth(140);
    historique.setShowGrid(false);
    historique.repaint();

    // Création du panneau d'infos de droite
    infos = new JPanel();
    infos.setPreferredSize(new Dimension((this.getWidth() - 20) * 1 / 3, this.getHeight() - 80));
    infos.setLayout(new FlowLayout(SwingConstants.CENTER));
    infos.setBackground(null);

    String trigrammeBanque = properties.getProperty("trigrammeBanque", "BOB");
    if (trigrammeBanque.equals("BOB")) {
      bobBanqueBouton = new JButton("BôB");
    } else {
      bobBanqueBouton = new JButton(trigrammeBanque);
    }
    bobBanqueBouton.setFont(new Font("ARIAL", Font.BOLD, 32));
    bobBanqueBouton.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 20) / 2, 60));

    binetBanqueBouton = new JButton("Mythe");
    binetBanqueBouton.setFont(new Font("ARIAL", Font.BOLD, 32));
    binetBanqueBouton.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 20) / 2, 60));

    trigrammeLabel = new JLabel();
    trigrammeLabel.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 20), 60));
    trigrammeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    trigrammeLabel.setFont(new Font("ARIAL", Font.BOLD, 40));

    nomLabel = new JTextPane();
    nomLabel.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 10), 150));
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

    ageLabel = new JLabel();
    ageLabel.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 10), 60));
    ageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    ageLabel.setFont(new Font("ARIAL", Font.BOLD, 25));

    balanceLabel = new JLabel();
    balanceLabel.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 10), 100));
    balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
    balanceLabel.setFont(new Font("ARIAL", Font.BOLD, 40));

    turnoverLabel = new JLabel();
    turnoverLabel.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 10), 40));
    turnoverLabel.setHorizontalAlignment(SwingConstants.CENTER);
    turnoverLabel.setFont(new Font("ARIAL", Font.PLAIN, 12));

    photo = new JLabel();
    photo.setPreferredSize(new Dimension((int) (infos.getPreferredSize().getWidth() - 10), 200));
    photo.setHorizontalAlignment(SwingConstants.CENTER);

    infos.add(bobBanqueBouton);
    infos.add(binetBanqueBouton);
    infos.add(trigrammeLabel);
    infos.add(nomLabel);
    infos.add(ageLabel);
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
    if (banqueBob.status != Trigramme.Binet) {
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

      LinkedList<Historique.Entry> histo = Historique.getHistorique(this, trigrammeActif, 50);
      for (Historique.Entry entry : histo) {
        String[] ligne = {entry.price + "", entry.banque, entry.admin, entry.comment, entry.localDate()};
        modele.addRow(ligne);
      }

      trigrammeLabel.setText(trigrammeActif.trigramme);
      String promo = "";
      if (trigrammeActif.promo > 0) {
        promo = "" + trigrammeActif.promo;
      }
      if (!trigrammeActif.nickname.equals("")) {
        nomLabel.setText(
            trigrammeActif.name + " " + trigrammeActif.first_name + " (" + trigrammeActif.nickname + ") " + promo);
      } else {
        nomLabel.setText(trigrammeActif.name + " " + trigrammeActif.first_name + " " + promo);
      }
      int age = trigrammeActif.getAge();
      if (age < 18) {
        ageLabel.setForeground(Color.RED);
        ageLabel.setFont(new Font("ARIAL", Font.BOLD, 50));
      } else {
        ageLabel.setForeground(Color.BLACK);
        ageLabel.setFont(new Font("ARIAL", Font.BOLD, 25));
      }
      if (age != -1) {
        ageLabel.setText(age + " ans");
      } else {
        ageLabel.setText("");
      }

      balanceLabel.setText("" + (double) trigrammeActif.balance / 100);
      if (trigrammeActif.status == 2) {
        turnoverLabel.setText(((double) (trigrammeActif.turnover) / 100) + "€ gagnés depuis dernier reset.");
      } else {
        turnoverLabel.setText(
            ((double) (trigrammeActif.turnover - trigrammeActif.balance) / 100) + "€ depensés depuis dernier reset.");
      }
      if (trigrammeActif.picture != "") {
        try {
          Image image = ImageIO.read(new File(trigrammeActif.picture));
          new ImageIcon(image);
          double zoom = Math.min((double) photo.getWidth() / (double) image.getWidth(null),
              (double) photo.getHeight() / (double) image.getHeight(null));
          photo.setIcon(new ImageIcon(image.getScaledInstance((int) (image.getWidth(null) * zoom),
              (int) (image.getHeight(null) * zoom), Image.SCALE_FAST)));
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
      ageLabel.setText("");
      balanceLabel.setText("");
      turnoverLabel.setText("");
      photo.setIcon(null);
    }
    infos.repaint();
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
      LinkedList<Historique.Entry> histo = Historique.getHistorique(this, trigrammeActif, -1);
      for (Historique.Entry entry : histo) {
        String[] ligne = {entry.price + "", entry.banque, entry.admin, entry.comment, entry.localDate()};
        modele.addRow(ligne);
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
      LinkedList<Historique.Entry> histo = Historique.getHistorique(this, trigrammeActif, -1);
      for (Historique.Entry entry : histo) {
        String[] ligne = {entry.price + "", entry.banque, entry.admin, entry.comment, entry.localDate()};
        modele.addRow(ligne);
      }
      LinkedList<Historique.Entry> oldhisto = Historique.getOldHistorique(this, trigrammeActif, -1);
      for (Historique.Entry entry : oldhisto) {
        String[] ligne = {entry.price + "", entry.banque, entry.admin, entry.comment, entry.localDate()};
        modele.addRow(ligne);
      }
      infos.repaint();
      historique.setModel(modele);
      historique.repaint();
      this.repaint();
    }
  }

  public static String formatString(String zou) {
    zou.replace(',', ';'); // Pour les exports csv.
    // Et on met les premieres lettres de chaque mot en majuscule.
    if (!zou.isEmpty()) {
      zou.substring(0, 0).toUpperCase();
    }
    for (int i = 1; i < zou.length(); ++i) {
      if (zou.charAt(i - 1) == ' ' || zou.charAt(i - 1) == '-') {
        zou.substring(i, i).toUpperCase();
      }
    }
    return zou;
  }

  public static int countOccurrences(String haystack, char needle) {
    int count = 0;
    for (char c : haystack.toCharArray()) {
      if (c == needle) {
        ++count;
      }
    }
    return count;
  }
}
