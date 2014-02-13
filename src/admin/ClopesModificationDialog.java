package admin;

import java.awt.Container;
import java.awt.Dimension;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import main.MainWindow;

public class ClopesModificationDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	ClopesModificationDialogListener listener = new ClopesModificationDialogListener(this);
	String marque;
	double prix;
	JTextField champMarque;
	JTextField champPrix;
	JButton okButton;
	JButton cancelButton;

	boolean validation = false;

	ClopesModificationDialog(MainWindow parent, String marque, double prix) {
		super(parent, "Modifier des clopes", true);
		this.parent = parent;
		this.marque = marque;
		this.prix = prix;
	}

	public void executer() throws Exception {

		JLabel labelTrigramme = new JLabel("Marque : ");
		labelTrigramme.setPreferredSize(new Dimension(120, 20));

		champMarque = new JTextField();
		champMarque.setPreferredSize(new Dimension(150, 20));
		champMarque.addKeyListener(listener);
		champMarque.setText(marque);
		champMarque.setEditable(false);

		JLabel labelPrix = new JLabel("Prix : ");
		labelPrix.setPreferredSize(new Dimension(120, 20));

		champPrix = new JTextField();
		champPrix.setPreferredSize(new Dimension(150, 20));
		champPrix.setText(prix + "");
		champPrix.addKeyListener(listener);

		okButton = new JButton("Valider");
		okButton.addActionListener(listener);
		okButton.setPreferredSize(new Dimension(140, 20));

		cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(listener);
		cancelButton.setPreferredSize(new Dimension(140, 20));

		JPanel pane = new JPanel();
		pane.add(labelTrigramme);
		pane.add(champMarque);
		pane.add(labelPrix);
		pane.add(champPrix);
		pane.add(okButton);
		pane.add(cancelButton);
		pane.setPreferredSize(new Dimension(300, 80));

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Container contentPane = this.getContentPane();
		contentPane.add(pane);
		this.pack();
		this.setLocation((parent.getWidth() - this.getWidth()) / 2,
				(parent.getHeight() - this.getHeight()) / 2);
		this.setResizable(false);
		this.setVisible(true);

		if (validation) {
			try {
				Statement stmt = parent.connexion.createStatement();
				stmt.executeUpdate("UPDATE clopes SET prix="
						+ (int) Math.round(100 * Double.parseDouble(champPrix.getText()))
						+ " WHERE marque='" + marque + "'");
			} catch (Exception e) {
				parent.afficherErreur(e);
			}
		}
	}

}