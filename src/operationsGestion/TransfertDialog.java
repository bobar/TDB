package operationsGestion;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import main.MainWindow;
import main.TDBException;
import main.Transaction;
import main.Trigramme;
import main.TrigrammeTextField;
import admin.AuthentificationDialog;

public class TransfertDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    MainWindow parent;
    TransfertDialogListener listener = new TransfertDialogListener();

    JFormattedTextField champTrigramme1;
    JFormattedTextField champTrigramme2;
    JTextField champMontant;
    JTextField champCommentaire;
    JButton okButton;
    JButton cancelButton;

    boolean validation = false;

    public class TransfertDialogListener implements KeyListener, ActionListener {

	public TransfertDialogListener() {
	    super();
	}

	public void keyPressed(KeyEvent arg0) {
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

    public TransfertDialog(MainWindow parent) {
	super(parent, "Transfert", true);
	this.parent = parent;
    }

    public void executer() throws Exception {

	AuthentificationDialog authentification = new AuthentificationDialog(parent);
	authentification.executer();

	if (authentification.droits >= AuthentificationDialog.Ami) {

	    JLabel labelTrigramme1 = new JLabel("De : ");
	    labelTrigramme1.setPreferredSize(new Dimension(120, 20));
	    champTrigramme1 = new TrigrammeTextField();
	    champTrigramme1.setPreferredSize(new Dimension(150, 20));
	    champTrigramme1.addKeyListener(listener);
	    if (parent.trigrammeActif != null) {
		champTrigramme1.setText(parent.trigrammeActif.trigramme);
	    }

	    JLabel labelTrigramme2 = new JLabel("Vers : ");
	    labelTrigramme2.setPreferredSize(new Dimension(120, 20));
	    champTrigramme2 = new TrigrammeTextField();
	    champTrigramme2.setPreferredSize(new Dimension(150, 20));
	    champTrigramme2.addKeyListener(listener);

	    JLabel labelMontant = new JLabel("Montant : ");
	    labelMontant.setPreferredSize(new Dimension(120, 20));
	    champMontant = new JTextField();
	    champMontant.setPreferredSize(new Dimension(150, 20));
	    champMontant.addKeyListener(listener);

	    JLabel labelCommentaire = new JLabel("Commentaire : ");
	    labelCommentaire.setPreferredSize(new Dimension(120, 20));
	    champCommentaire = new JTextField();
	    champCommentaire.setPreferredSize(new Dimension(150, 20));
	    champCommentaire.addKeyListener(listener);

	    okButton = new JButton("Valider");
	    okButton.addActionListener(listener);
	    okButton.setPreferredSize(new Dimension(140, 20));

	    cancelButton = new JButton("Annuler");
	    cancelButton.addActionListener(listener);
	    cancelButton.setPreferredSize(new Dimension(140, 20));

	    JPanel pane = new JPanel();
	    pane.add(labelTrigramme1);
	    pane.add(champTrigramme1);
	    pane.add(labelTrigramme2);
	    pane.add(champTrigramme2);
	    pane.add(labelMontant);
	    pane.add(champMontant);
	    pane.add(labelCommentaire);
	    pane.add(champCommentaire);
	    pane.add(okButton);
	    pane.add(cancelButton);
	    pane.setPreferredSize(new Dimension(300, 130));

	    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	    Container contentPane = this.getContentPane();
	    contentPane.add(pane);
	    this.pack();
	    this.setLocation((parent.getWidth() - this.getWidth()) / 2,
		    (parent.getHeight() - this.getHeight()) / 2);
	    this.setResizable(false);
	    this.setVisible(true);

	    if (validation) {
		int montant = (int) (100 * Double.parseDouble(champMontant.getText()));
		String commentaire = champCommentaire.getText();
		if (montant < 0) {
		    Trigramme trigramme1 = new Trigramme(parent, champTrigramme1.getText());
		    Trigramme trigramme2 = new Trigramme(parent, champTrigramme2.getText());
		    Statement stmt = parent.connexion.createStatement();
		    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + (-montant)
			    + " WHERE id=" + trigramme1.id);
		    Date date = new Date();
		    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    String dateString = formater.format(date);
		    Transaction transaction =
			    new Transaction(trigramme1.id, -montant, commentaire,
				    authentification.admin, dateString, trigramme2.id);
		    parent.dernieresActions.add(transaction);
		    stmt.executeUpdate("INSERT INTO transactions (id,price,comment,admin,date,id2) VALUES ("
			    + transaction.id
			    + ","
			    + transaction.price
			    + ",'"
			    + transaction.comment
			    + "',"
			    + transaction.admin
			    + ",'"
			    + transaction.date
			    + "',"
			    + transaction.id2 + ")");

		    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + (-montant)
			    + " WHERE id=" + trigramme2.id);
		    stmt.closeOnCompletion();
		} else {
		    Trigramme trigramme1 = new Trigramme(parent, champTrigramme1.getText());
		    Trigramme trigramme2 = new Trigramme(parent, champTrigramme2.getText());
		    Statement stmt = parent.connexion.createStatement();
		    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + montant
			    + " WHERE id=" + trigramme1.id);
		    Date date = new Date();
		    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    String dateString = formater.format(date);
		    Transaction transaction =
			    new Transaction(trigramme1.id, -montant, commentaire,
				    authentification.admin, dateString, trigramme2.id);
		    parent.dernieresActions.add(transaction);
		    stmt.executeUpdate("INSERT INTO transactions (id,price,comment,admin,date,id2) VALUES ("
			    + transaction.id
			    + ","
			    + transaction.price
			    + ",'"
			    + transaction.comment
			    + "',"
			    + transaction.admin
			    + ",'"
			    + transaction.date
			    + "',"
			    + transaction.id2 + ")");
		    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + montant
			    + " WHERE id=" + trigramme2.id);
		    stmt.closeOnCompletion();
		}
	    }
	    parent.refresh();
	} else {
	    throw new TDBException("Vous n'avez pas les droits");
	}
    }
}