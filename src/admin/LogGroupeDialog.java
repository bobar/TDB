package admin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import main.MainWindow;
import main.TDBException;
import main.Transaction;
import main.Trigramme;

public class LogGroupeDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    MainWindow parent;
    LogGroupeDialogListener listener = new LogGroupeDialogListener();

    JTextField champMontant;
    JTextField champCommentaire;
    JTextField champTrigramme;
    JButton okButton;
    JButton cancelButton;

    boolean validation = false;

    public class LogGroupeDialogListener implements KeyListener, ActionListener {

	public LogGroupeDialogListener() {
	    super();
	}

	public void keyPressed(KeyEvent arg0) {
	    if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
		try {
		    int confirmation =
			    JOptionPane.showConfirmDialog(parent, "Etes-vous sur ?",
				    "Confirmation", JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE, null);
		    if (confirmation == JOptionPane.YES_OPTION) {
			String trig = champTrigramme.getText().toUpperCase();
			trig.replace(" ", ",");
			trig.replace(";", ",");
			trig.replace(".", ",");
			String[] trigrammes = trig.split(",");

			String trigrammesFaux = "Trigrammes faux : ";
			for (int i = 0; i < trigrammes.length; i++) {
			    Statement stmt = parent.connexion.createStatement();
			    ResultSet rs =
				    stmt.executeQuery("SELECT id FROM accounts WHERE trigramme ='"
					    + trigrammes[i] + "'");
			    if (!rs.first()) {
				trigrammesFaux += trigrammes[i] + " ";
			    }
			}
			if (!trigrammesFaux.equals("Trigrammes faux : ")) {
			    JOptionPane.showMessageDialog(parent, trigrammesFaux, "Erreurs",
				    JOptionPane.WARNING_MESSAGE, null);
			} else {
			    validation = true;
			    dispose();
			}
		    }
		} catch (Exception e) {
		    parent.afficherErreur(e);
		}
	    } else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
		validation = false;
		dispose();
	    }
	}

	public void keyReleased(KeyEvent arg0) {}

	public void keyTyped(KeyEvent arg0) {}

	public void actionPerformed(ActionEvent arg0) {
	    if (arg0.getSource().equals(okButton)) {
		try {
		    int confirmation =
			    JOptionPane.showConfirmDialog(parent, "Etes-vous sur ?",
				    "Confirmation", JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE, null);
		    if (confirmation == JOptionPane.YES_OPTION) {
			String trig = champTrigramme.getText().toUpperCase();
			trig.replace(" ", ",");
			trig.replace(";", ",");
			trig.replace(".", ",");
			String[] trigrammes = trig.split(",");

			String trigrammesFaux = "Trigrammes faux : ";
			for (int i = 0; i < trigrammes.length; i++) {
			    Statement stmt = parent.connexion.createStatement();
			    ResultSet rs =
				    stmt.executeQuery("SELECT id FROM accounts WHERE trigramme ='"
					    + trigrammes[i] + "'");
			    if (!rs.first()) {
				trigrammesFaux += trigrammes[i] + " ";
			    }
			}
			if (!trigrammesFaux.equals("Trigrammes faux : ")) {
			    JOptionPane.showMessageDialog(parent, trigrammesFaux, "Erreurs",
				    JOptionPane.WARNING_MESSAGE, null);
			} else {
			    validation = true;
			    dispose();
			}
		    }
		} catch (Exception e) {
		    parent.afficherErreur(e);
		}
	    } else if (arg0.getSource().equals(cancelButton)) {
		validation = false;
		dispose();
	    }
	}
    }

    public LogGroupeDialog(MainWindow parent) {
	super(parent, "Achat groupé", true);
	this.parent = parent;
    }

    public void executer() throws Exception {

	AuthentificationDialog authentification = new AuthentificationDialog(parent);
	authentification.executer();

	if (authentification.droits >= AuthentificationDialog.Ami) {

	    JLabel labelMontant = new JLabel("Montant : ");
	    labelMontant.setPreferredSize(new Dimension(110, 20));
	    champMontant = new JTextField();
	    champMontant.setPreferredSize(new Dimension(190, 20));
	    champMontant.addKeyListener(listener);

	    JLabel labelCommentaire = new JLabel("Commentaire : ");
	    labelCommentaire.setPreferredSize(new Dimension(110, 20));
	    champCommentaire = new JTextField();
	    champCommentaire.setPreferredSize(new Dimension(190, 20));
	    champCommentaire.addKeyListener(listener);

	    JLabel labelTrigramme = new JLabel("Sur : ");
	    labelTrigramme.setPreferredSize(new Dimension(110, 20));
	    champTrigramme = new JTextField();
	    champTrigramme.setPreferredSize(new Dimension(190, 20));
	    champTrigramme.addKeyListener(listener);

	    okButton = new JButton("Valider");
	    okButton.addActionListener(listener);
	    okButton.setPreferredSize(new Dimension(140, 20));

	    cancelButton = new JButton("Annuler");
	    cancelButton.addActionListener(listener);
	    cancelButton.setPreferredSize(new Dimension(140, 20));

	    JPanel pane = new JPanel();
	    pane.add(labelMontant);
	    pane.add(champMontant);
	    pane.add(labelCommentaire);
	    pane.add(champCommentaire);
	    pane.add(labelTrigramme);
	    pane.add(champTrigramme);
	    pane.add(okButton);
	    pane.add(cancelButton);
	    pane.setPreferredSize(new Dimension(330, 110));

	    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	    Container contentPane = this.getContentPane();
	    contentPane.add(pane);
	    this.pack();
	    this.setLocation((parent.getWidth() - this.getWidth()) / 2,
		    (parent.getHeight() - this.getHeight()) / 2);
	    this.setResizable(false);
	    this.setVisible(true);

	    if (validation) {

		String commentaire = champCommentaire.getText();
		GregorianCalendar date = new GregorianCalendar();
		date.setTime(new Date());

		String trig = champTrigramme.getText().toUpperCase();
		trig.replace(" ", ",");
		trig.replace(";", ",");
		trig.replace(".", ",");
		String[] trigrammes = trig.split(",");

		String trigrammesFaux = "Trigrammes faux : ";
		for (int i = 0; i < trigrammes.length; i++) {
		    Statement stmt = parent.connexion.createStatement();
		    ResultSet rs =
			    stmt.executeQuery("SELECT id FROM accounts WHERE trigramme ='"
				    + trigrammes[i] + "'");
		    if (!rs.first()) {
			trigrammesFaux += trigrammes[i] + " ";
		    }
		}
		if (!trigrammesFaux.equals("Trigrammes faux : ")) {
		    JOptionPane.showMessageDialog(parent, trigrammesFaux, "Erreurs",
			    JOptionPane.WARNING_MESSAGE, null);
		}

		int montant =
			10 * (int) Math.ceil(10 * Double.parseDouble(champMontant.getText())
				/ trigrammes.length);

		if (montant > 0) {
		    for (int i = 0; i < trigrammes.length; i++) {
			Trigramme trigramme = new Trigramme(parent, trigrammes[i]);
			Statement stmt = parent.connexion.createStatement();
			stmt.executeUpdate("UPDATE accounts SET balance=balance-" + montant
				+ " WHERE id=" + trigramme.id);
			Transaction transaction =
				new Transaction(trigramme.id, -montant, commentaire,
					authentification.admin,
					(int) (date.getTimeInMillis() / 1000), parent.banqueBob.id);
			stmt.executeUpdate("INSERT INTO transactions (id,price,comment,admin,date,id2) VALUES ("
				+ transaction.id
				+ ","
				+ transaction.price
				+ ",'"
				+ transaction.comment
				+ "',"
				+ transaction.admin
				+ ","
				+ transaction.date + "," + transaction.id2 + ")");
			stmt.executeUpdate("UPDATE accounts SET balance=balance+" + montant
				+ " WHERE id=" + parent.banqueBob.id);
		    }
		} else {
		    throw new TDBException("Montant négatif");
		}
	    }
	    parent.refresh();
	} else {
	    throw new TDBException("Vous n'avez pas les droits");
	}

    }
}