package admin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import main.Droits;
import main.MainWindow;
import main.TDBException;

public class DroitsCreationDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	protected MainWindow parent;
	private DroitsCreationDialogListener listener = new DroitsCreationDialogListener();

	private JTextField champNom;
	protected JButton okButton;
	protected JButton cancelButton;
	private Vector<JCheckBox> droitsBox = new Vector<JCheckBox>();
	private Vector<JLabel> droitsLabel = new Vector<JLabel>();

	protected boolean validation = false;

	public class DroitsCreationDialogListener implements KeyListener, ActionListener {

		protected DroitsCreationDialogListener() {
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

	protected DroitsCreationDialog(MainWindow parent) {
		super(parent, "Création de droits", true);
		this.parent = parent;
	}

	protected void executer() throws Exception {

		JLabel labelNom = new JLabel("Nom : ");
		labelNom.setPreferredSize(new Dimension(120, 20));

		champNom = new JTextField();
		champNom.setPreferredSize(new Dimension(150, 20));
		champNom.addKeyListener(listener);

		okButton = new JButton("Valider");
		okButton.addActionListener(listener);
		okButton.setPreferredSize(new Dimension(140, 20));

		cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(listener);
		cancelButton.setPreferredSize(new Dimension(140, 20));

		JPanel pane = new JPanel();
		pane.add(labelNom);
		pane.add(champNom);
		for (String droit : Droits.nom_droits) {
			JCheckBox droitBox = new JCheckBox();
			droitBox.setFocusable(false);
			droitBox.addKeyListener(listener);
			droitsBox.add(droitBox);
			JLabel droitLabel = new JLabel(droit);
			droitLabel.setPreferredSize(new Dimension(200, 20));
			droitsLabel.add(droitLabel);
			pane.add(droitLabel);
			pane.add(droitBox);
		}
		pane.add(okButton);
		pane.add(cancelButton);
		pane.setPreferredSize(new Dimension(300, 470));

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Container contentPane = this.getContentPane();
		contentPane.add(pane);
		this.pack();
		this.setLocation((parent.getWidth() - this.getWidth()) / 2,
				(parent.getHeight() - this.getHeight()) / 2);
		this.setResizable(false);
		this.setVisible(true);

		if (validation) {
			if (Droits.existe(parent, champNom.getText())) {
				throw new TDBException("Nom déjà pris.");
			}
			Vector<String> granted = new Vector<String>();
			for (int i = 0; i < droitsBox.size(); ++i) {
				if (droitsBox.get(i).isSelected()) {
					granted.add(Droits.possibilites[i]);
				}
			}
			System.out.println(champNom.getText());
			Droits droits = new Droits(parent, champNom.getText(), granted);
			droits.creer();
		}
	}
}