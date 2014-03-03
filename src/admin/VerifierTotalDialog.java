package admin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import main.MainWindow;
import main.TDBException;

public class VerifierTotalDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    MainWindow parent;
    VerifierTotalDialogListener listener = new VerifierTotalDialogListener();

    int totalActuel;
    int bobActuel;
    int bobIdeal;
    JLabel valueTotal;
    JLabel valueBobActuel;
    JLabel valueBobIdeal;
    JButton okButton;
    JButton cancelButton;

    boolean validation = false;

    public class VerifierTotalDialogListener implements KeyListener, ActionListener {

	public VerifierTotalDialogListener() {
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
			Statement stmt = parent.connexion.createStatement();
			stmt.executeUpdate("UPDATE accounts SET balance=" + bobIdeal
				+ " WHERE trigramme='" + parent.banqueBob.trigramme + "'");
			dispose();
		    }
		} catch (Exception e) {
		    dispose();
		    parent.afficherErreur(e);
		}
	    } else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
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
			Statement stmt = parent.connexion.createStatement();
			stmt.executeUpdate("UPDATE accounts SET balance=" + bobIdeal
				+ " WHERE trigramme='" + parent.banqueBob.trigramme + "'");
			dispose();
		    }
		} catch (Exception e) {
		    dispose();
		    parent.afficherErreur(e);
		}
	    } else if (arg0.getSource().equals(cancelButton)) {
		dispose();
	    }
	}
    }

    public VerifierTotalDialog(MainWindow parent) {
	super(parent, "Achat groupé", true);
	this.parent = parent;
    }

    public void executer() throws Exception {

	AuthentificationDialog authentification = new AuthentificationDialog(parent);
	authentification.executer();

	if (authentification.droits >= AuthentificationDialog.BoBarman) {

	    Statement stmt = parent.connexion.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT SUM(balance) as tot FROM accounts");
	    if (rs.next()) totalActuel = Integer.parseInt(rs.getString("tot"));
	    rs =
		    stmt.executeQuery("SELECT balance FROM accounts WHERE trigramme='"
			    + parent.banqueBob.trigramme + "'");
	    if (rs.next()) bobActuel = Integer.parseInt(rs.getString("balance"));
	    bobIdeal = bobActuel - totalActuel;

	    JLabel labelTotal = new JLabel("Total : ");
	    labelTotal.setPreferredSize(new Dimension(110, 20));
	    valueTotal = new JLabel("" + (double) totalActuel / 100);
	    valueTotal.setPreferredSize(new Dimension(100, 20));

	    JLabel labelBobActuel = new JLabel("BôB : ");
	    labelBobActuel.setPreferredSize(new Dimension(110, 20));
	    valueBobActuel = new JLabel("" + (double) bobActuel / 100);
	    valueBobActuel.setPreferredSize(new Dimension(100, 20));

	    JLabel labelBobIdeal = new JLabel("Idéal : ");
	    labelBobIdeal.setPreferredSize(new Dimension(110, 20));
	    valueBobIdeal = new JLabel("" + (double) bobIdeal / 100);
	    valueBobIdeal.setPreferredSize(new Dimension(100, 20));

	    okButton = new JButton("Actualiser");
	    okButton.addActionListener(listener);
	    okButton.setPreferredSize(new Dimension(140, 20));

	    cancelButton = new JButton("Quitter");
	    cancelButton.addActionListener(listener);
	    cancelButton.setPreferredSize(new Dimension(140, 20));

	    JPanel pane = new JPanel();
	    pane.add(labelTotal);
	    pane.add(valueTotal);
	    pane.add(labelBobActuel);
	    pane.add(valueBobActuel);
	    pane.add(labelBobIdeal);
	    pane.add(valueBobIdeal);
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

	    parent.refresh();
	} else {
	    throw new TDBException("Vous n'avez pas les droits");
	}
    }
}