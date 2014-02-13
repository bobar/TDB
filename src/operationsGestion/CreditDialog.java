package operationsGestion;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import main.MainWindow;
import main.TDBException;
import admin.AuthentificationDialog;

public class CreditDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	CreditDialogListener listener = new CreditDialogListener(this);

	JTextField champMontant;
	JComboBox<String> modeDeDepot;
	JTextField champCommentaire;
	JButton okButton;
	JButton cancelButton;

	boolean validation = false;

	public CreditDialog(MainWindow parent, String debut) {
		super(parent, "Crediter un trigramme", true);
		this.parent = parent;
	}

	public void executer() throws Exception {

		AuthentificationDialog authentification = new AuthentificationDialog(parent);
		authentification.executer();
		if (authentification.droits >= AuthentificationDialog.Ami) {
			champMontant = new JTextField();
			champMontant.setPreferredSize(new Dimension(180, 20));
			champMontant.addKeyListener(listener);
			JLabel montantLabel = new JLabel("Montant : ");
			montantLabel.setPreferredSize(new Dimension(130, 20));
			montantLabel.setHorizontalAlignment(SwingConstants.RIGHT);

			String[] choix = { "Liquide", "Chèque" };
			modeDeDepot = new JComboBox<String>(choix);
			modeDeDepot.setPreferredSize(new Dimension(180, 20));
			modeDeDepot.addKeyListener(listener);
			JLabel modeLabel = new JLabel("Mode : ");
			modeLabel.setPreferredSize(new Dimension(130, 20));
			modeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

			champCommentaire = new JTextField();
			champCommentaire.setPreferredSize(new Dimension(180, 20));
			champCommentaire.addKeyListener(listener);
			JLabel commentaireLabel = new JLabel("Commentaire : ");
			commentaireLabel.setPreferredSize(new Dimension(130, 20));
			commentaireLabel.setHorizontalAlignment(SwingConstants.RIGHT);

			okButton = new JButton("Valider");
			okButton.addActionListener(listener);
			okButton.setPreferredSize(new Dimension(150, 20));

			cancelButton = new JButton("Annuler");
			cancelButton.addActionListener(listener);
			cancelButton.setPreferredSize(new Dimension(150, 20));

			JPanel pane = new JPanel();
			pane.add(montantLabel);
			pane.add(champMontant);
			pane.add(modeLabel);
			pane.add(modeDeDepot);
			pane.add(commentaireLabel);
			pane.add(champCommentaire);
			pane.add(okButton);
			pane.add(cancelButton);
			pane.setPreferredSize(new Dimension(350, 110));

			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.getContentPane().add(pane);
			this.pack();
			this.setLocation((parent.getWidth() - this.getWidth()) / 2,
					(parent.getHeight() - this.getHeight()) / 2);
			this.setResizable(false);
			this.setVisible(true);

			if (validation) {
				int montant = (int) (100 * Double.parseDouble(champMontant.getText()));
				String comment = "";
				if (champCommentaire.getText().equals("")) {
					comment = (String) modeDeDepot.getSelectedItem();
				} else {
					comment = champCommentaire.getText();
				}
				parent.trigrammeActif.crediter(parent.connexion, montant, comment,
						authentification.admin);
			}
		} else {
			throw new TDBException("Vous n'avez pas les droits");
		}
	}

}
