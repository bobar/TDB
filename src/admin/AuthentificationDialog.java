package admin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.WindowConstants;
import main.MainWindow;
import main.TDBException;
import main.TrigrammeTextField;

public class AuthentificationDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public static final int Pekin = 0;
    public static final int Ami = 1;
    public static final int ExBoBarman = 2;
    public static final int BoBarman = 3;

    MainWindow parent;
    AuthentificationDialogListener listener = new AuthentificationDialogListener();

    JFormattedTextField champTrigramme;
    JPasswordField champMDP;
    JButton okButton;
    JButton cancelButton;

    public int admin = 0;
    public int droits = -1;
    boolean validation = false;

    public class AuthentificationDialogListener implements KeyListener, ActionListener {

	public AuthentificationDialogListener() {
	    super();
	}

	public void keyPressed(KeyEvent arg0) {
	    repaint();
	    if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
		validation = true;
		dispose();
	    } else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
		validation = false;
		dispose();
	    }
	}

	public void keyReleased(KeyEvent arg0) {}

	public void keyTyped(KeyEvent arg0) {}

	public void actionPerformed(ActionEvent e) {
	    if (e.getSource().equals(okButton)) {
		validation = true;
		dispose();
	    } else if (e.getSource().equals(cancelButton)) {
		validation = false;
		dispose();
	    }

	}

    }

    public AuthentificationDialog(MainWindow parent) {
	super(parent, "Authentification", true);
	this.parent = parent;
    }

    public void executer() throws Exception {

	if (parent.modeAdministrateur) {
	    this.admin = parent.administrateur.id;
	    this.droits = 3;
	} else {
	    JLabel labelTrigramme = new JLabel("Trigramme : ");
	    labelTrigramme.setPreferredSize(new Dimension(120, 20));

	    champTrigramme = new TrigrammeTextField();

	    champTrigramme.setPreferredSize(new Dimension(150, 20));
	    champTrigramme.addKeyListener(listener);
	    JLabel labelMDP = new JLabel("Mot de passe : ");
	    labelMDP.setPreferredSize(new Dimension(120, 20));
	    champMDP = new JPasswordField();
	    champMDP.setPreferredSize(new Dimension(150, 20));
	    champMDP.addKeyListener(listener);

	    okButton = new JButton("Valider");
	    okButton.addActionListener(listener);
	    okButton.setPreferredSize(new Dimension(140, 20));

	    cancelButton = new JButton("Annuler");
	    cancelButton.addActionListener(listener);
	    cancelButton.setPreferredSize(new Dimension(140, 20));

	    JPanel pane = new JPanel();
	    pane.add(labelTrigramme);
	    pane.add(champTrigramme);
	    pane.add(labelMDP);
	    pane.add(champMDP);
	    pane.add(okButton);
	    pane.add(cancelButton);
	    pane.setPreferredSize(new Dimension(300, 80));

	    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	    Container contentPane = this.getContentPane();
	    contentPane.add(pane);
	    this.pack();
	    this.setLocation((parent.getWidth() - this.getWidth()) / 2, (parent.getHeight() - this.getHeight()) / 2);
	    this.setResizable(false);
	    this.setVisible(true);

	    if (validation) {
		String trigramme = champTrigramme.getText();
		String cryptage = MD5Hex(champMDP.getText());
		try {
		    Statement stmt = parent.connexion.createStatement();
		    ResultSet rs = stmt
			    .executeQuery("Select accounts.id as admin_id, permissions FROM admins LEFT JOIN accounts on accounts.id = admins.id WHERE accounts.trigramme = '"
				    + trigramme + "' AND passwd = '" + cryptage + "'");
		    if (rs.next()) {
			this.admin = rs.getInt("admin_id");
			this.droits = rs.getInt("permissions");
		    } else {
			throw new TDBException("Erreur d'authentification");
		    }
		} catch (Exception e) {
		    parent.afficherErreur(e);
		}
	    }
	}
    }

    public static String MD5Hex(String s) {
	String result = null;
	try {
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	    byte[] digest = md5.digest(s.getBytes());
	    result = toHex(digest);
	} catch (NoSuchAlgorithmException e) {
	    // this won't happen, we know Java has MD5!
	}
	return result;
    }

    public static String toHex(byte[] a) {
	StringBuilder sb = new StringBuilder(a.length * 2);
	for (int i = 0; i < a.length; i++) {
	    sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
	    sb.append(Character.forDigit(a[i] & 0x0f, 16));
	}
	return sb.toString();
    }
}
