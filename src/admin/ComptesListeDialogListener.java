package admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.Export;
import main.Trigramme;

public class ComptesListeDialogListener implements ActionListener, KeyListener {

	ComptesListeDialog parent;

	ComptesListeDialogListener(ComptesListeDialog parent) {
		this.parent = parent;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
			parent.dispose();
		} else if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
			int ligneChoisie = parent.resultats.getSelectedRow();
			try {
				parent.parent.setTrigrammeActif(new Trigramme(parent.parent,
						(String) parent.resultats.getValueAt(ligneChoisie, 0)));
				parent.dispose();
			} catch (Exception e) {
				parent.parent.afficherErreur(e);
			}
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
		if (arg0.getSource() == parent.fermerButton) {
			parent.dispose();
		} else if (arg0.getSource().equals(parent.sauverButton)) {
			try {
				Export export = new Export(parent.parent);
				export.sauverListe(parent.resultats);
			} catch (Exception e) {
				parent.parent.afficherErreur(e);
			}
		} else if (arg0.getSource() == parent.ouvrirButton) {
			int ligneChoisie = parent.resultats.getSelectedRow();
			try {
				parent.parent.setTrigrammeActif(new Trigramme(parent.parent,
						(String) parent.resultats.getValueAt(ligneChoisie, 0)));
				parent.dispose();
			} catch (Exception e) {
				parent.parent.afficherErreur(e);
			}
		}
	}

}