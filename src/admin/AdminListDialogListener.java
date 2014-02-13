package admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Statement;

import javax.swing.JOptionPane;

import main.TDBException;
import main.Trigramme;

public class AdminListDialogListener implements ActionListener, KeyListener {

	AdminListDialog parent;

	AdminListDialogListener(AdminListDialog dialog) {
		this.parent = dialog;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
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
		try {
			if (arg0.getSource().equals(parent.creerButton)) {
				AdminCreationDialog dialog = new AdminCreationDialog(parent.parent);
				dialog.executer();
			} else if (arg0.getSource().equals(parent.modifierButton)) {
				int ligneChoisie = parent.listeAdmin.getSelectedRow();
				if (ligneChoisie == -1) {
					throw new TDBException("Pas d'admin sélectionné");
				}
				int permissions = 0;
				String perms = (String) parent.listeAdmin.getValueAt(ligneChoisie, 3);
				if (perms.equals("BôBarman")) {
					permissions = 3;
				} else if (perms.equals("Ex-BôBarman")) {
					permissions = 2;
				} else if (perms.equals("Ami du BôB")) {
					permissions = 1;
				} else {
					permissions = 0;
				}
				AdminModificationDialog dialog = new AdminModificationDialog(parent.parent,
						(String) parent.listeAdmin.getValueAt(ligneChoisie, 0), permissions);
				dialog.executer();
			} else if (arg0.getSource().equals(parent.supprimerButton)) {
				int ligneChoisie = parent.listeAdmin.getSelectedRow();
				if (ligneChoisie == -1) {
					throw new TDBException("Pas d'admin sélectionné");
				}
				Trigramme trigramme = new Trigramme(parent.parent,
						(String) parent.listeAdmin.getValueAt(ligneChoisie, 0));
				int confirmation = JOptionPane.showConfirmDialog(parent.parent,
						"Etes-vous sur de vouloir supprimer " + trigramme.trigramme + " ("
								+ trigramme.name + " " + trigramme.first_name + ")",
						"Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
						null);
				if (confirmation == JOptionPane.OK_OPTION) {
					Statement stmt = parent.parent.connexion.createStatement();
					stmt.executeUpdate("DELETE FROM admins WHERE id=" + trigramme.id);
				}
			} else if (arg0.getSource() == parent.fermerButton) {
				parent.dispose();
			}
			parent.refresh();
		} catch (Exception e) {
			parent.parent.afficherErreur(e);
		}
	}
}