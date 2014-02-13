package admin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import main.MainWindow;

public class ComptesChoixDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    MainWindow parent;
    ComptesChoixDialogListener listener = new ComptesChoixDialogListener(this);

    JCheckBox caseXPlatal;// status 0
    JCheckBox caseXAncien;// 1
    JCheckBox caseBinet;// 2
    JCheckBox casePersonnel;// 3
    JCheckBox caseEtudiant;// 4
    JCheckBox caseAutre;// 5
    JCheckBox casePromoMin;
    JFormattedTextField champPromoMin;
    JCheckBox casePromoMax;
    JFormattedTextField champPromoMax;
    JCheckBox caseSoldeMin;
    JTextField champSoldeMin;
    JCheckBox caseSoldeMax;
    JTextField champSoldeMax;
    JButton okButton;
    JButton fermerButton;

    JPanel fond;

    public class ComptesChoixDialogListener implements ActionListener, KeyListener {

	ComptesChoixDialog parent;

	ComptesChoixDialogListener(ComptesChoixDialog dialog) {
	    super();
	    this.parent = dialog;
	}

	public void keyPressed(KeyEvent arg0) {
	    if (arg0.getSource().equals(champPromoMin)) {
		casePromoMin.setSelected(true);
	    } else if (arg0.getSource().equals(champPromoMax)) {
		casePromoMax.setSelected(true);
	    } else if (arg0.getSource().equals(champSoldeMin)) {
		caseSoldeMin.setSelected(true);
	    } else if (arg0.getSource().equals(champSoldeMax)) {
		caseSoldeMax.setSelected(true);
	    } else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
		dispose();
	    } else if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
		try {
		    dispose();
		    ComptesListeDialog dialog = new ComptesListeDialog(parent);
		    dialog.executer();
		    dialog.chargerListe();
		} catch (Exception e) {
		    parent.parent.afficherErreur(e);
		}
	    }
	}

	public void keyReleased(KeyEvent arg0) {}

	public void keyTyped(KeyEvent arg0) {}

	public void actionPerformed(ActionEvent arg0) {
	    if (arg0.getSource() == okButton) try {
		dispose();
		ComptesListeDialog dialog = new ComptesListeDialog(parent);
		dialog.executer();
		dialog.chargerListe();
	    } catch (Exception e) {
		parent.parent.afficherErreur(e);
	    }
	    if (arg0.getSource() == fermerButton) {
		dispose();
	    }
	}

    }

    public ComptesChoixDialog(MainWindow parent) {
	super(parent, "Paramètres", true);
	this.parent = parent;
    }

    public void executer() throws Exception {

	AuthentificationDialog authentification = new AuthentificationDialog(parent);
	authentification.executer();

	if (authentification.droits == AuthentificationDialog.BoBarman) {

	    this.addKeyListener(listener);

	    caseXPlatal = new JCheckBox();
	    caseXPlatal.setFocusable(false);
	    caseXPlatal.setSelected(true);
	    caseXPlatal.addKeyListener(listener);

	    JLabel labelXPlatal = new JLabel("X plataliens");
	    labelXPlatal.setPreferredSize(new Dimension(180, 20));

	    caseXAncien = new JCheckBox();
	    caseXAncien.setFocusable(false);
	    caseXAncien.setSelected(true);
	    caseXAncien.addKeyListener(listener);

	    JLabel labelXAncien = new JLabel("X anciens");
	    labelXAncien.setPreferredSize(new Dimension(180, 20));

	    caseBinet = new JCheckBox();
	    caseBinet.setFocusable(false);
	    caseBinet.setSelected(true);
	    caseBinet.addKeyListener(listener);

	    JLabel labelBinet = new JLabel("Binet");
	    labelBinet.setPreferredSize(new Dimension(180, 20));

	    casePersonnel = new JCheckBox();
	    casePersonnel.setFocusable(false);
	    casePersonnel.setSelected(true);
	    casePersonnel.addKeyListener(listener);

	    JLabel labelPersonnel = new JLabel("Personnel");
	    labelPersonnel.setPreferredSize(new Dimension(180, 20));

	    caseEtudiant = new JCheckBox();
	    caseEtudiant.setFocusable(false);
	    caseEtudiant.setSelected(true);
	    caseEtudiant.addKeyListener(listener);

	    JLabel labelEtudiant = new JLabel("Etudiant non X");
	    labelEtudiant.setPreferredSize(new Dimension(180, 20));

	    caseAutre = new JCheckBox();
	    caseAutre.setFocusable(false);
	    caseAutre.setSelected(true);
	    caseAutre.addKeyListener(listener);

	    JLabel labelAutre = new JLabel("Autre");
	    labelAutre.setPreferredSize(new Dimension(180, 20));

	    casePromoMin = new JCheckBox();
	    casePromoMin.setFocusable(false);
	    casePromoMin.addKeyListener(listener);

	    JLabel labelPromoMin = new JLabel("Promo min : ");
	    labelPromoMin.setPreferredSize(new Dimension(100, 20));

	    champPromoMin = new JFormattedTextField(new MaskFormatter("####"));
	    champPromoMin.setPreferredSize(new Dimension(75, 20));
	    champPromoMin.addKeyListener(listener);

	    casePromoMax = new JCheckBox();
	    casePromoMax.setFocusable(false);
	    casePromoMax.addKeyListener(listener);

	    JLabel labelPromoMax = new JLabel("Promo max : ");
	    labelPromoMax.setPreferredSize(new Dimension(100, 20));

	    champPromoMax = new JFormattedTextField(new MaskFormatter("####"));
	    champPromoMax.setPreferredSize(new Dimension(75, 20));
	    champPromoMax.addKeyListener(listener);

	    // en fait, je sais pas si ca sert vraiment, on s'en fout de
	    // connaitre que les gens riches
	    caseSoldeMin = new JCheckBox();
	    caseSoldeMin.setFocusable(false);
	    caseSoldeMin.addKeyListener(listener);

	    JLabel labelSoldeMin = new JLabel("Solde min : ");
	    labelSoldeMin.setPreferredSize(new Dimension(100, 20));

	    champSoldeMin = new JTextField();
	    champSoldeMin.setPreferredSize(new Dimension(75, 20));
	    champSoldeMin.addKeyListener(listener);

	    caseSoldeMax = new JCheckBox();
	    caseSoldeMax.setFocusable(false);
	    caseSoldeMax.addKeyListener(listener);

	    JLabel labelSoldeMax = new JLabel("Solde max : ");
	    labelSoldeMax.setPreferredSize(new Dimension(100, 20));

	    champSoldeMax = new JTextField();
	    champSoldeMax.setPreferredSize(new Dimension(75, 20));
	    champSoldeMax.addKeyListener(listener);

	    okButton = new JButton("Valider");
	    okButton.setPreferredSize(new Dimension(90, 20));
	    okButton.addActionListener(listener);

	    fermerButton = new JButton("Annuler");
	    fermerButton.setPreferredSize(new Dimension(90, 20));
	    fermerButton.addActionListener(listener);

	    fond = new JPanel();
	    fond.setLayout(new FlowLayout(FlowLayout.CENTER));
	    fond.add(caseXPlatal);
	    fond.add(labelXPlatal);
	    fond.add(caseXAncien);
	    fond.add(labelXAncien);
	    fond.add(caseBinet);
	    fond.add(labelBinet);
	    fond.add(casePersonnel);
	    fond.add(labelPersonnel);
	    fond.add(caseEtudiant);
	    fond.add(labelEtudiant);
	    fond.add(caseAutre);
	    fond.add(labelAutre);
	    fond.add(casePromoMin);
	    fond.add(labelPromoMin);
	    fond.add(champPromoMin);
	    fond.add(casePromoMax);
	    fond.add(labelPromoMax);
	    fond.add(champPromoMax);
	    fond.add(caseSoldeMin);
	    fond.add(labelSoldeMin);
	    fond.add(champSoldeMin);
	    fond.add(caseSoldeMax);
	    fond.add(labelSoldeMax);
	    fond.add(champSoldeMax);
	    fond.add(okButton);
	    fond.add(fermerButton);

	    fond.setPreferredSize(new Dimension(230, 290));
	    fond.setOpaque(true);

	    this.setContentPane(fond);
	    this.pack();
	    this.setLocation((parent.getWidth() - this.getWidth()) / 2, (parent.getHeight() - this.getHeight()) / 2);
	    this.setResizable(false);
	    this.setVisible(true);
	}
    }
}
