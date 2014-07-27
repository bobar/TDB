package operationsGestion;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import main.AuthException;
import main.MainWindow;
import admin.AuthentificationDialog;

public class MailDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	CreditDialogListener listener = new CreditDialogListener();

	JTextField champMail;
	JButton okButton;
	JButton cancelButton;

	boolean validation = false;

	public class CreditDialogListener implements KeyListener, ActionListener {

		public CreditDialogListener() {
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

	public MailDialog(MainWindow parent) {
		super(parent, "Définir adresse mail", true);
		this.parent = parent;
	}

	public void executer() throws Exception {

		AuthentificationDialog authentification = new AuthentificationDialog(parent);
		authentification.executer();
		if (!authentification.admin.has_droit("modifier_tri")) {
			throw new AuthException();
		}
		JLabel mailActuelLabel = new JLabel("Mail actuel : ");
		mailActuelLabel.setPreferredSize(new Dimension(130, 20));
		mailActuelLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel mailLabel = new JLabel(parent.trigrammeActif.getMail());
		mailLabel.setPreferredSize(new Dimension(280, 20));
		mailLabel.setHorizontalAlignment(SwingConstants.LEFT);

		champMail = new JTextField();
		champMail.setPreferredSize(new Dimension(280, 20));
		champMail.addKeyListener(listener);
		JLabel newMailLabel = new JLabel("Nouveau mail : ");
		newMailLabel.setPreferredSize(new Dimension(130, 20));
		newMailLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		okButton = new JButton("Valider");
		okButton.addActionListener(listener);
		okButton.setPreferredSize(new Dimension(150, 20));

		cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(listener);
		cancelButton.setPreferredSize(new Dimension(150, 20));

		JPanel pane = new JPanel();
		pane.add(mailActuelLabel);
		pane.add(mailLabel);
		pane.add(newMailLabel);
		pane.add(champMail);
		pane.add(okButton);
		pane.add(cancelButton);
		pane.setPreferredSize(new Dimension(450, 80));

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.getContentPane().add(pane);
		this.pack();
		this.setLocation((parent.getWidth() - this.getWidth()) / 2,
				(parent.getHeight() - this.getHeight()) / 2);
		this.setResizable(false);
		this.setVisible(true);

		if (validation) {
			String newMail = champMail.getText();
			if (!newMail.isEmpty()) {
				parent.trigrammeActif.setMail(newMail);
			}
		}
	}

}
