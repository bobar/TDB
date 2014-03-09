package operationsStandard;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import main.MainWindow;
import main.TDBException;
import main.Transaction;
import main.Trigramme;
import admin.AuthentificationDialog;

public class ClopesDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    MainWindow parent;
    ClopesDialogListener listener = new ClopesDialogListener();

    JComboBox<String> champMarque;
    JSpinner champQuantite;
    JButton okButton;
    JButton cancelButton;
    JPanel fond;

    boolean validation = false;

    public class ClopesDialogListener implements KeyListener, ActionListener {

	public ClopesDialogListener() {
	    super();
	}

	public void keyPressed(KeyEvent arg0) {
	    if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
		if (!champMarque.isPopupVisible()) {
		    validation = true;
		    dispose();
		}
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

    public ClopesDialog(MainWindow parent) {
	super(parent, "Acheter des clopes", true);
	this.parent = parent;
    }

    public void executer() throws Exception {

	this.addKeyListener(listener);

	JLabel labelMarque = new JLabel("Marque : ");
	labelMarque.setPreferredSize(new Dimension(100, 20));
	labelMarque.setHorizontalAlignment(SwingConstants.RIGHT);

	Statement stmt = parent.connexion.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT marque,prix FROM clopes ORDER BY quantite DESC");
	LinkedList<String> listeMarques = new LinkedList<String>();
	while (rs.next()) {
	    listeMarques.add(rs.getString("marque"));
	}
	String[] tableauMarques = listeMarques.toArray(new String[0]);
	champMarque = new JComboBox<String>(tableauMarques);
	champMarque.setPreferredSize(new Dimension(150, 20));
	champMarque.addKeyListener(listener);

	JLabel labelQuantite = new JLabel("Quantité : ");
	labelQuantite.setPreferredSize(new Dimension(100, 20));
	labelQuantite.setHorizontalAlignment(SwingConstants.RIGHT);

	SpinnerNumberModel modeleQuantite = new SpinnerNumberModel(1, 1, 20, 1);
	champQuantite = new JSpinner(modeleQuantite);
	champQuantite.setPreferredSize(new Dimension(150, 20));
	((JSpinner.DefaultEditor) champQuantite.getEditor()).getTextField()
		.addKeyListener(listener);

	okButton = new JButton("Valider");
	okButton.setPreferredSize(new Dimension(100, 20));
	okButton.addActionListener(listener);

	cancelButton = new JButton("Annuler");
	cancelButton.setPreferredSize(new Dimension(100, 20));
	cancelButton.addActionListener(listener);

	fond = new JPanel();
	fond.add(labelMarque);
	fond.add(champMarque);
	fond.add(labelQuantite);
	fond.add(champQuantite);
	fond.add(okButton);
	fond.add(cancelButton);

	fond.setPreferredSize(new Dimension(300, 90));
	fond.setOpaque(true);

	this.setContentPane(fond);
	this.pack();
	this.setLocation((parent.getWidth() - this.getWidth()) / 2,
		(parent.getHeight() - this.getHeight()) / 2);
	this.setResizable(false);
	this.setVisible(true);

	if (validation) {

	    String marque = (String) champMarque.getSelectedItem();
	    int quantite = (Integer) champQuantite.getValue();
	    ResultSet rs2 =
		    stmt.executeQuery("SELECT prix FROM clopes WHERE marque='" + marque + "'");
	    int prix = 0;
	    if (rs2.next()) {
		prix = rs2.getInt("prix");
	    }
	    if (prix == 0) { throw new TDBException("Problème d`accès à la base des clopes"); }

	    int admin = 0;
	    if (quantite * prix > 2000
		    || (parent.trigrammeActif.status != Trigramme.XPlatal && parent.trigrammeActif.balance < quantite
			    * prix)) {
		AuthentificationDialog authentification = new AuthentificationDialog(parent);
		authentification.executer();

		if (authentification.droits < AuthentificationDialog.Ami) {
		    throw new TDBException("vous n`avez pas les droits");
		} else {
		    admin = authentification.admin;
		}
	    }
	    stmt.executeUpdate("UPDATE accounts SET balance=balance-" + (prix * quantite)
		    + " WHERE id=" + parent.trigrammeActif.id);
	    Date date = new Date();
	    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String dateString = formater.format(date);
	    Transaction transaction =
		    new Transaction(parent.trigrammeActif.id, -quantite * prix, quantite + " "
			    + marque, admin, dateString, parent.banqueBob.id);
	    parent.dernieresActions.add(transaction);
	    stmt.executeUpdate("INSERT INTO transactions (id,price,comment,admin,date,id2) VALUES ("
		    + transaction.id
		    + ","
		    + transaction.price
		    + ",'"
		    + transaction.comment
		    + "',"
		    + transaction.admin + ",'" + transaction.date + "'," + transaction.id2 + ")");
	    stmt.executeUpdate("UPDATE accounts SET balance=balance+" + (prix * quantite)
		    + " WHERE id=" + parent.banqueBob.id);
	    stmt.closeOnCompletion();

	    stmt.executeUpdate("UPDATE clopes SET quantite=quantite+" + quantite
		    + " WHERE marque='" + marque + "'");
	}
	parent.refresh();
    }

}