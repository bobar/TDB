package operationsStandard;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import main.Admin;
import main.Clopes;
import main.MainWindow;
import main.TDBException;
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

	LinkedList<String> listeMarques = Clopes.getMarques(parent);
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
	    
	    Clopes clopes = new Clopes(parent, marque);

	    Admin admin = null;
	    if (quantite * clopes.prix() > 2000
		    || (parent.trigrammeActif.status != Trigramme.XPlatal && parent.trigrammeActif.balance < quantite
			    * clopes.prix())) {
		AuthentificationDialog authentification = new AuthentificationDialog(parent);
		authentification.executer();

		if (authentification.admin.ami()) {
		    admin = authentification.admin;
		} else {
		    throw new TDBException("vous n'avez pas les droits");
		}
	    }
	    clopes.Vendre(parent.trigrammeActif,admin,quantite);
	}
	parent.refresh();
    }

}