package operationsStandard;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import main.MainWindow;

public class DebitDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	DebitDialogListener listener = new DebitDialogListener(this);

	String debut;
	JTextField champMontant;
	JButton okButton;
	JButton cancelButton;

	boolean validation = false;

	public DebitDialog(MainWindow parent, String debut) {
		super(parent, "Debiter un trigramme", true);
		this.parent = parent;
		this.debut = debut;
	}

	public void executer() throws Exception {

		JLabel labelMontant = new JLabel("Montant : ");
		labelMontant.setPreferredSize(new Dimension(120, 20));
		labelMontant.setHorizontalAlignment(SwingConstants.RIGHT);
		champMontant = new JTextField("");
		champMontant.setPreferredSize(new Dimension(100, 20));
		champMontant.setText(debut);
		champMontant.addKeyListener(listener);

		okButton = new JButton("Valider");
		okButton.addActionListener(listener);
		okButton.setPreferredSize(new Dimension(120, 20));

		cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(listener);
		cancelButton.setPreferredSize(new Dimension(120, 20));

		JPanel pane = new JPanel();
		pane.add(labelMontant);
		pane.add(champMontant);
		pane.add(okButton);
		pane.add(cancelButton);
		pane.setPreferredSize(new Dimension(350, 60));

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
			parent.trigrammeActif.debiter(montant);
		}
	}

}
