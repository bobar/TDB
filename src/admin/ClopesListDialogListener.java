package admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Statement;

import javax.swing.JOptionPane;

import main.TDBException;

public class ClopesListDialogListener implements ActionListener, KeyListener {

	ClopesListDialog parent;

	ClopesListDialogListener(ClopesListDialog dialog) {
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
				ClopesCreationDialog dialog = new ClopesCreationDialog(parent.parent);
				dialog.executer();
			} else if (arg0.getSource().equals(parent.modifierButton)) {
				int ligneChoisie = parent.listeClopes.getSelectedRow();
				if (ligneChoisie == -1) {
					throw new TDBException("Pas de clopes sélectionnées");
				}
				double prix = Double.parseDouble((String) parent.listeClopes.getValueAt(
						ligneChoisie, 1));
				ClopesModificationDialog dialog = new ClopesModificationDialog(parent.parent,
						(String) parent.listeClopes.getValueAt(ligneChoisie, 0), prix);
				dialog.executer();
			} else if (arg0.getSource().equals(parent.supprimerButton)) {
				int ligneChoisie = parent.listeClopes.getSelectedRow();
				if (ligneChoisie == -1) {
					throw new TDBException("Pas de clopes sélectionnées");
				}
				int confirmation = JOptionPane.showConfirmDialog(
						parent.parent,
						"Etes-vous sur de vouloir supprimer "
								+ parent.modele.getValueAt(ligneChoisie, 0) + " ?", "Confirmation",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
				if (confirmation == JOptionPane.OK_OPTION) {
					Statement stmt = parent.parent.connexion.createStatement();
					stmt.executeUpdate("DELETE FROM clopes WHERE marque='"
							+ parent.modele.getValueAt(ligneChoisie, 0) + "'");
				}
			} else if (arg0.getSource().equals(parent.resetButton)) {
				int confirmation = JOptionPane.showConfirmDialog(parent.parent,
						"Etes-vous sur de vouloir remettre les quantités à 0 ?", "Confirmation",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
				if (confirmation == JOptionPane.OK_OPTION) {
					Statement stmt = parent.parent.connexion.createStatement();
					stmt.executeUpdate("UPDATE clopes SET quantite =0");
				}
			} else if (arg0.getSource().equals(parent.fermerButton)) {
				parent.dispose();
			}
			parent.refresh();
		} catch (Exception e) {
			parent.parent.afficherErreur(e);
		}
	}
}