package admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ComptesChoixDialogListener implements ActionListener, KeyListener {

	ComptesChoixDialog parent;

	ComptesChoixDialogListener(ComptesChoixDialog parent) {
		this.parent = parent;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getSource().equals(parent.champPromoMin)) {
			parent.casePromoMin.setSelected(true);
		} else if (arg0.getSource().equals(parent.champPromoMax)) {
			parent.casePromoMax.setSelected(true);
		} else if (arg0.getSource().equals(parent.champSoldeMin)) {
			parent.caseSoldeMin.setSelected(true);
		} else if (arg0.getSource().equals(parent.champSoldeMax)) {
			parent.caseSoldeMax.setSelected(true);
		} else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
			parent.dispose();
		} else if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
			try {
				parent.dispose();
				ComptesListeDialog dialog = new ComptesListeDialog(parent);
				dialog.executer();
				dialog.chargerListe();
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
		if (arg0.getSource() == parent.okButton)
			try {
				parent.dispose();
				ComptesListeDialog dialog = new ComptesListeDialog(parent);
				dialog.executer();
				dialog.chargerListe();
			} catch (Exception e) {
				parent.parent.afficherErreur(e);
			}
		if (arg0.getSource() == parent.fermerButton) {
			parent.dispose();
		}
	}

}
