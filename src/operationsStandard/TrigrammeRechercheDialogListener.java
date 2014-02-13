package operationsStandard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import main.Trigramme;

public class TrigrammeRechercheDialogListener implements ActionListener, KeyListener, MouseListener {

	TrigrammeRechercheDialog parent;

	TrigrammeRechercheDialogListener(TrigrammeRechercheDialog parent) {
		this.parent = parent;
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
			parent.dispose();
		} else if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
			try {
				parent.chargerListe();
			} catch (Exception e) {
				parent.parent.afficherErreur(e);
			}
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
		try {
			parent.chargerListe();
		} catch (Exception e) {
			parent.parent.afficherErreur(e);
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == parent.fermerButton) {
			parent.dispose();
		} else if (arg0.getSource() == parent.ouvrirButton) {
			int ligneChoisie = parent.resultats.getSelectedRow();
			try {
				parent.parent.setTrigrammeActif(new Trigramme(parent.parent,
						(String) parent.resultats.getValueAt(ligneChoisie, 0)));
				parent.dispose();
			} catch (Exception e) {
				parent.parent.afficherErreur(e);
			}
		} else if (arg0.getSource() == parent.rechercheButton) {
			try {
				parent.chargerListe();
			} catch (Exception e) {
				parent.parent.afficherErreur(e);
			}
		}
	}

	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount() == 2) {
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

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}
}
