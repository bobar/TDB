package admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class LogGroupeDialogListener implements KeyListener, ActionListener {

	private LogGroupeDialog parent;

	public LogGroupeDialogListener(LogGroupeDialog dialog) {
		super();
		this.parent = dialog;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
			try {
				int confirmation = JOptionPane.showConfirmDialog(parent, "Etes-vous sur ?",
						"Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null);
				if (confirmation == JOptionPane.YES_OPTION) {
					String trig = parent.champTrigramme.getText().toUpperCase();
					trig.replace(" ", ",");
					trig.replace(";", ",");
					trig.replace(".", ",");
					String[] trigrammes = trig.split(",");

					String trigrammesFaux = "Trigrammes faux : ";
					for (int i = 0; i < trigrammes.length; i++) {
						Statement stmt = parent.parent.connexion.createStatement();
						ResultSet rs = stmt
								.executeQuery("SELECT id FROM accounts WHERE trigramme ='"
										+ trigrammes[i] + "'");
						if (!rs.first()) {
							trigrammesFaux += trigrammes[i] + " ";
						}
					}
					if (!trigrammesFaux.equals("Trigrammes faux : ")) {
						JOptionPane.showMessageDialog(parent, trigrammesFaux, "Erreurs",
								JOptionPane.WARNING_MESSAGE, null);
					} else {
						parent.validation = true;
						parent.dispose();
					}
				}
			} catch (Exception e) {
				parent.parent.afficherErreur(e);
			}
		} else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
			parent.validation = false;
			parent.dispose();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(parent.okButton)) {
			try {
				int confirmation = JOptionPane.showConfirmDialog(parent, "Etes-vous sur ?",
						"Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null);
				if (confirmation == JOptionPane.YES_OPTION) {
					String trig = parent.champTrigramme.getText().toUpperCase();
					trig.replace(" ", ",");
					trig.replace(";", ",");
					trig.replace(".", ",");
					String[] trigrammes = trig.split(",");

					String trigrammesFaux = "Trigrammes faux : ";
					for (int i = 0; i < trigrammes.length; i++) {
						Statement stmt = parent.parent.connexion.createStatement();
						ResultSet rs = stmt
								.executeQuery("SELECT id FROM accounts WHERE trigramme ='"
										+ trigrammes[i] + "'");
						if (!rs.first()) {
							trigrammesFaux += trigrammes[i] + " ";
						}
					}
					if (!trigrammesFaux.equals("Trigrammes faux : ")) {
						JOptionPane.showMessageDialog(parent, trigrammesFaux, "Erreurs",
								JOptionPane.WARNING_MESSAGE, null);
					} else {
						parent.validation = true;
						parent.dispose();
					}
				}
			} catch (Exception e) {
				parent.parent.afficherErreur(e);
			}
		} else if (arg0.getSource().equals(parent.cancelButton)) {
			parent.validation = false;
			parent.dispose();
		}
	}
}