package operationsStandard;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.MainWindow;
import main.Trigramme;

public class TrigrammeDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	TrigrammeDialogListener listener = new TrigrammeDialogListener(this);

	JTextField champSaisie;
	String debut = "";

	boolean validation = false;

	public TrigrammeDialog(MainWindow parent, String debut) {
		super(parent, "Saisir un trigramme", true);
		this.parent = parent;
		this.debut = debut;
	}

	public void executer() throws Exception {

		champSaisie = new JTextField();
		champSaisie.setPreferredSize(new Dimension(40, 20));
		champSaisie.setText(debut);
		champSaisie.addKeyListener(listener);

		JPanel pane = new JPanel();
		pane.add(champSaisie);
		pane.setPreferredSize(new Dimension(300, 40));

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		Container contentPane = this.getContentPane();
		contentPane.add(pane);
		this.pack();
		this.setLocation((parent.getWidth() - this.getWidth()) / 2,
				(parent.getHeight() - this.getHeight()) / 2);
		this.setResizable(false);
		this.setVisible(true);

		if (validation) {
			parent.setTrigrammeActif(new Trigramme(parent, champSaisie.getText()));
		}
	}
}
