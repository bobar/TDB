package admin;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import main.Export;
import main.MainWindow;
import main.TDBException;

public class PositivationDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	PositivationDialogListener listener = new PositivationDialogListener(this);

	JTextField champPromo;
	JTextField champSeuil;
	JButton okButton;
	JButton cancelButton;

	boolean validation = false;

	public PositivationDialog(MainWindow parent) {
		super(parent, "Positivation", true);
		this.parent = parent;
	}

	public void executer() throws Exception {

		AuthentificationDialog authentification = new AuthentificationDialog(this.parent);
		authentification.executer();

		if (authentification.droits == AuthentificationDialog.BoBarman) {
			champPromo = new JTextField();
			champPromo.setPreferredSize(new Dimension(180, 20));
			champPromo.addKeyListener(listener);
			JLabel labelPromo = new JLabel("Promo : ");
			labelPromo.setPreferredSize(new Dimension(130, 20));
			labelPromo.setHorizontalAlignment(SwingConstants.RIGHT);

			champSeuil = new JTextField();
			champSeuil.setPreferredSize(new Dimension(180, 20));
			champSeuil.addKeyListener(listener);
			champSeuil.setText("0");
			JLabel labelSeuil = new JLabel("Seuil : ");
			labelSeuil.setPreferredSize(new Dimension(130, 20));
			labelSeuil.setHorizontalAlignment(SwingConstants.RIGHT);

			okButton = new JButton("Valider");
			okButton.addActionListener(listener);
			okButton.setPreferredSize(new Dimension(150, 20));

			cancelButton = new JButton("Annuler");
			cancelButton.addActionListener(listener);
			cancelButton.setPreferredSize(new Dimension(150, 20));

			JPanel pane = new JPanel();
			pane.add(labelPromo);
			pane.add(champPromo);
			pane.add(labelSeuil);
			pane.add(champSeuil);
			pane.add(okButton);
			pane.add(cancelButton);
			pane.setPreferredSize(new Dimension(350, 80));

			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.getContentPane().add(pane);
			this.pack();
			this.setLocation((parent.getWidth() - this.getWidth()) / 2,
					(parent.getHeight() - this.getHeight()) / 2);
			this.setResizable(false);
			this.setVisible(true);

			if (validation) {
				int seuil = (int) (100 * Double.parseDouble(champSeuil.getText()));
				int promo = Integer.parseInt(champPromo.getText());
				Export export = new Export(parent);
				export.positivation(seuil, promo);
			}

		} else {
			throw new TDBException("Vous n'avez pas les droits");
		}
	}
}