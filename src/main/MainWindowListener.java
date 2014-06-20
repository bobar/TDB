package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import operationsGestion.CreditDialog;
import operationsGestion.MailDialog;
import operationsGestion.TransfertDialog;
import operationsGestion.TrigrammeCreationDialog;
import operationsGestion.TrigrammeModificationDialog;
import operationsStandard.ClopesDialog;
import operationsStandard.DebitDialog;
import operationsStandard.TrigrammeDialog;
import operationsStandard.TrigrammeRechercheDialog;
import admin.AdminListDialog;
import admin.BinetsListeDialog;
import admin.ClopesListDialog;
import admin.ComptesChoixDialog;
import admin.DebitFichierDialog;
import admin.DroitsListDialog;
import admin.LogGroupeDialog;
import admin.MotDePasseDialog;
import admin.PositivationDialog;
import admin.VerifierTotalDialog;

public class MainWindowListener implements KeyListener, ActionListener {

	private MainWindow parent;

	MainWindowListener(MainWindow parent) {
		super();
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent action) {
		try {
			if (action.getSource().equals(parent.ouvrirTrigramme)) {
				TrigrammeDialog dialog = new TrigrammeDialog(parent, "");
				dialog.executer();
			} else if (action.getSource().equals(parent.fermerTrigramme)) {
				parent.fermerTrigrammeActif();
			} else if (action.getSource().equals(parent.rechercherTrigramme)) {
				TrigrammeRechercheDialog rechercheDialog = new TrigrammeRechercheDialog(parent);
				rechercheDialog.executer();
			} else if (action.getSource().equals(parent.voirHistorique)) {
				if (parent.trigrammeActif != null) {
					parent.refreshHistorique();
				}
			} else if (action.getSource().equals(parent.voirAncienHistorique)) {
				if (parent.trigrammeActif != null) {
					parent.refreshOldHistorique();
				}
			} else if (action.getSource().equals(parent.debiterTrigramme)) {
				if (parent.trigrammeActif == null) {
					TrigrammeDialog dialog = new TrigrammeDialog(parent, "");
					dialog.executer();
				}
				if (parent.trigrammeActif != null) {
					DebitDialog dialog = new DebitDialog(parent, "");
					dialog.executer();
				}
			} else if (action.getSource().equals(parent.acheterClopes)) {
				if (parent.trigrammeActif == null) {
					TrigrammeDialog dialog = new TrigrammeDialog(parent, "");
					dialog.executer();
				}
				if (parent.trigrammeActif != null) {
					ClopesDialog dialog = new ClopesDialog(parent);
					dialog.executer();
				}
			} else if (action.getSource().equals(parent.pinteDeKroPourSIE)) {
				parent.setTrigrammeActif(new Trigramme(parent, "SIE"));
				parent.banqueBobActif = true;
				parent.trigrammeActif.debiter(200);
				parent.refresh();
			} else if (action.getSource().equals(parent.annuler)) {
				parent.annuler();
			} else if (action.getSource().equals(parent.fasciserParMail)) {
				if (parent.trigrammeActif == null) {
					TrigrammeDialog dialog = new TrigrammeDialog(parent, "");
					dialog.executer();
				}
				if (parent.trigrammeActif != null) {
					parent.trigrammeActif.sendPolytechniqueMail();
				}
			} else if (action.getSource().equals(parent.loggerAPlusieurs)) {
				LogGroupeDialog dialog = new LogGroupeDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.approvisionner)) {
				if (parent.trigrammeActif == null) {
					TrigrammeDialog dialog = new TrigrammeDialog(parent, "");
					dialog.executer();
				}
				if (parent.trigrammeActif != null) {
					CreditDialog dialog = new CreditDialog(parent, "");
					dialog.executer();
				}
			} else if (action.getSource().equals(parent.transfert)) {
				TransfertDialog dialog = new TransfertDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.modifierMail)) {
				MailDialog dialog = new MailDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.creerTrigramme)) {
				TrigrammeCreationDialog dialog = new TrigrammeCreationDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.modifierTrigramme)) {
				TrigrammeModificationDialog dialog = new TrigrammeModificationDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.supprimerTrigramme)) {
				parent.supprimerTrigramme();
			} else if (action.getSource().equals(parent.verifTotal)) {
				VerifierTotalDialog dialog = new VerifierTotalDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.voirBinets)) {
				BinetsListeDialog dialog = new BinetsListeDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.voirComptes)) {
				ComptesChoixDialog dialog = new ComptesChoixDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.debiterFichier)) {
				DebitFichierDialog dialog = new DebitFichierDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.positivation)) {
				PositivationDialog dialog = new PositivationDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.exporter)) {
				Export export = new Export(parent);
				export.exporterBase();
			} else if (action.getSource().equals(parent.reinitialiserHistorique)) {
				parent.reinitialiserHistorique();
			} else if (action.getSource().equals(parent.reinitialiserConso)) {
				parent.reinitialiserTurnover();
			} else if (action.getSource().equals(parent.ouvrirModeAdmin)) {
				parent.ouvrirModeAdmin();
			} else if (action.getSource().equals(parent.fermerModeAdmin)) {
				parent.fermerModeAdmin();
			} else if (action.getSource().equals(parent.ouvrirBinet)) {
				parent.ouvrirBanqueBinet();
			} else if (action.getSource().equals(parent.fermerBinet)) {
				parent.fermerBanqueBinet();
			} else if (action.getSource().equals(parent.changerMDP)) {
				MotDePasseDialog dialog = new MotDePasseDialog(parent);
				dialog.executer();
			} else if (action.getSource().equals(parent.gestionAdmins)) {
				AdminListDialog dialog = new AdminListDialog(parent);
				dialog.executer();
				dialog.refresh();
			} else if (action.getSource().equals(parent.gestionDroits)) {
				DroitsListDialog dialog = new DroitsListDialog(parent);
				dialog.executer();
				dialog.refresh();
			} else if (action.getSource().equals(parent.gestionClopes)) {
				ClopesListDialog dialog = new ClopesListDialog(parent);
				dialog.executer();
				dialog.refresh();
			} else if (action.getSource().equals(parent.bobBanqueBouton)) {
				parent.banqueBobActif = true;
				parent.refresh();
			} else if (action.getSource().equals(parent.binetBanqueBouton)) {
				if (parent.banqueBinet != null) {
					parent.banqueBobActif = false;
					parent.refresh();
				} else {
					parent.afficherMythe();
				}
			}
		} catch (Exception e) {
			parent.afficherErreur(e);
		}

	}

	public void keyPressed(KeyEvent arg0) {
		try {
			if ((arg0.getKeyChar() >= 'a' && arg0.getKeyChar() <= 'z')
					|| (arg0.getKeyChar() >= 'A' && arg0.getKeyChar() <= 'Z')) {
				TrigrammeDialog dialog =
						new TrigrammeDialog(parent, ("" + arg0.getKeyChar()).toUpperCase());
				dialog.executer();
			} else if ((arg0.getKeyChar() >= '0' && arg0.getKeyChar() <= '9')
					|| arg0.getKeyChar() == '.') {
				if (parent.trigrammeActif == null) {
					TrigrammeDialog dialog = new TrigrammeDialog(parent, "");
					dialog.executer();
				}
				DebitDialog dialog = new DebitDialog(parent, arg0.getKeyChar() + "");
				dialog.executer();
			} else if (arg0.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
				parent.annuler();
			} else if (arg0.getKeyChar() == KeyEvent.VK_TAB) {
				if (parent.banqueBinet != null) {
					parent.banqueBobActif = !parent.banqueBobActif;
					parent.refresh();
				}
			} else if (arg0.getKeyChar() == KeyEvent.VK_LEFT) {
				parent.banqueBobActif = true;
			} else if (arg0.getKeyChar() == KeyEvent.VK_RIGHT) {
				parent.banqueBobActif = false;
			}
		} catch (Exception e) {
			parent.afficherErreur(e);
		}
	}

	public void keyReleased(KeyEvent arg0) {}

	public void keyTyped(KeyEvent arg0) {}
}