package admin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import main.Export;
import main.MainWindow;
import main.Trigramme;

public class ComptesListeDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	ComptesChoixDialog choix;
	ComptesListeDialogListener listener = new ComptesListeDialogListener();

	JButton ouvrirButton;
	JButton sauverButton;
	JButton fermerButton;
	JButton envoyerMails;

	JPanel fond;
	JTable resultats;
	DefaultTableModel modele = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int rowIndex, int columnInder) {
			return false;
		}
	};
	DefaultTableColumnModel modeleColonnes;
	JScrollPane resultatsScrollPane;

	public class ComptesListeDialogListener implements ActionListener, KeyListener {

		ComptesListeDialogListener() {}

		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
				dispose();
			} else if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
				int ligneChoisie = resultats.getSelectedRow();
				try {
					parent.setTrigrammeActif(new Trigramme(parent, (String) resultats.getValueAt(
							ligneChoisie, 0)));
					dispose();
				} catch (Exception e) {
					parent.afficherErreur(e);
				}
			}
		}

		public void keyReleased(KeyEvent arg0) {}

		public void keyTyped(KeyEvent arg0) {}

		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == fermerButton) {
				dispose();
			} else if (arg0.getSource().equals(sauverButton)) {
				try {
					Export export = new Export(parent);
					export.sauverListe(resultats);
				} catch (Exception e) {
					parent.afficherErreur(e);
				}
			} else if (arg0.getSource().equals(ouvrirButton)) {
				int ligneChoisie = resultats.getSelectedRow();
				try {
					parent.setTrigrammeActif(new Trigramme(parent, (String) resultats.getValueAt(
							ligneChoisie, 0)));
					dispose();
				} catch (Exception e) {
					parent.afficherErreur(e);
				}
			} else if (arg0.getSource().equals(envoyerMails)) {
				int confirmation =
						JOptionPane.showConfirmDialog(parent,
								"Etes-vous sur de vouloir envoyer les mails ?", "Confirmation",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
				if (confirmation == JOptionPane.YES_OPTION) {
					try {
						int nbLignes = resultats.getModel().getRowCount();
						for (int i = 0; i < nbLignes; ++i) {
							Trigramme trig =
									new Trigramme(parent, (String) modele.getValueAt(i, 0));
							trig.sendPolytechniqueMail();
						}
						JOptionPane.showMessageDialog(parent, nbLignes + " mails envoyés.",
								"Mails envoyés", JOptionPane.INFORMATION_MESSAGE, null);
					} catch (Exception e) {
						parent.afficherErreur(e);
					}
				}
			}
		}

	}

	ComptesListeDialog(ComptesChoixDialog choix) {
		super(choix.parent, "Liste des comptes", true);
		this.parent = choix.parent;
		this.choix = choix;
	}

	public void executer() throws Exception {

		this.addKeyListener(listener);

		resultats = new JTable();
		resultats.addKeyListener(listener);
		resultats.setModel(modele);
		resultats.repaint();

		resultatsScrollPane = new JScrollPane(resultats);
		resultatsScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		resultatsScrollPane.setPreferredSize(new Dimension(450, 460));

		String[] header = { "Trigramme", "Nom", "Prénom", "Balance" };
		modele.setColumnIdentifiers(header);

		resultats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultats.setModel(modele);
		resultats.getColumnModel().getColumn(0).setPreferredWidth(65);
		resultats.getColumnModel().getColumn(1).setPreferredWidth(150);
		resultats.getColumnModel().getColumn(2).setPreferredWidth(150);
		resultats.getColumnModel().getColumn(2).setPreferredWidth(65);
		resultats.repaint();

		resultats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ouvrirButton = new JButton("Ouvrir le trigramme");
		ouvrirButton.setPreferredSize(new Dimension(190, 20));
		ouvrirButton.addActionListener(listener);

		sauverButton = new JButton("Sauver");
		sauverButton.setPreferredSize(new Dimension(100, 20));
		sauverButton.addActionListener(listener);

		fermerButton = new JButton("Fermer la fenêtre");
		fermerButton.setPreferredSize(new Dimension(160, 20));
		fermerButton.addActionListener(listener);

		envoyerMails = new JButton("Envoyer mails de fascisation");
		envoyerMails.setPreferredSize(new Dimension(250, 20));
		envoyerMails.addActionListener(listener);

		fond = new JPanel();
		fond.setLayout(new FlowLayout(FlowLayout.CENTER));
		fond.add(resultatsScrollPane);
		fond.add(ouvrirButton);
		fond.add(sauverButton);
		fond.add(fermerButton);
		fond.add(envoyerMails);

		fond.setPreferredSize(new Dimension(500, 520));
		fond.setOpaque(true);

		this.setContentPane(fond);
		this.pack();
		this.setLocation((parent.getWidth() - this.getWidth()) / 2,
				(parent.getHeight() - this.getHeight()) / 2);
		this.chargerListe();
		this.setResizable(false);
		this.setVisible(true);
	}

	void chargerListe() throws Exception {
		for (int i = modele.getRowCount() - 1; i >= 0; i--) {
			modele.removeRow(i);
		}
		PreparedStatement stmt =
				parent.connexion
						.prepareStatement("SELECT trigramme,name,first_name,balance FROM accounts WHERE status = ? AND promo >= ? AND promo <= ? AND balance >= ? AND balance <= ?");
		if (choix.casePromoMin.isSelected()) {
			stmt.setInt(2, Integer.parseInt(choix.champPromoMin.getText()));
		} else {
			stmt.setInt(2, Integer.MIN_VALUE);
		}
		if (choix.casePromoMax.isSelected()) {
			stmt.setInt(3, Integer.parseInt(choix.champPromoMax.getText()));
		} else {
			stmt.setInt(3, Integer.MAX_VALUE);
		}
		if (choix.caseSoldeMin.isSelected()) {
			stmt.setInt(4, (int) (100 * Double.parseDouble(choix.champSoldeMin.getText())));
		} else {
			stmt.setInt(4, Integer.MIN_VALUE);
		}
		if (choix.caseSoldeMax.isSelected()) {
			stmt.setInt(5, (int) (100 * Double.parseDouble(choix.champSoldeMax.getText())));
		} else {
			stmt.setInt(5, Integer.MAX_VALUE);
		}

		if (choix.caseXPlatal.isSelected()) {
			stmt.setInt(1, Trigramme.XPlatal);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String[] item =
						{ rs.getString("trigramme"), rs.getString("name"),
								rs.getString("first_name"),
								"" + ((double) (rs.getInt("balance")) / 100) };
				modele.addRow(item);
			}
		}
		if (choix.caseXAncien.isSelected()) {
			stmt.setInt(1, Trigramme.XAncien);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String[] item =
						{ rs.getString("trigramme"), rs.getString("name"),
								rs.getString("first_name"),
								"" + ((double) (rs.getInt("balance")) / 100) };
				modele.addRow(item);
			}
		}
		if (choix.caseBinet.isSelected()) {
			stmt.setInt(1, Trigramme.Binet);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String[] item =
						{ rs.getString("trigramme"), rs.getString("name"),
								rs.getString("first_name"),
								"" + ((double) (rs.getInt("balance")) / 100) };
				modele.addRow(item);
			}
		}
		if (choix.casePersonnel.isSelected()) {
			stmt.setInt(1, Trigramme.Personnel);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String[] item =
						{ rs.getString("trigramme"), rs.getString("name"),
								rs.getString("first_name"),
								"" + ((double) (rs.getInt("balance")) / 100) };
				modele.addRow(item);
			}
		}
		if (choix.caseEtudiant.isSelected()) {
			stmt.setInt(1, Trigramme.Etudiant);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String[] item =
						{ rs.getString("trigramme"), rs.getString("name"),
								rs.getString("first_name"),
								"" + ((double) (rs.getInt("balance")) / 100) };
				modele.addRow(item);
			}
		}
		if (choix.caseAutre.isSelected()) {
			stmt.setInt(1, Trigramme.Autre);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String[] item =
						{ rs.getString("trigramme"), rs.getString("name"),
								rs.getString("first_name"),
								"" + ((double) (rs.getInt("balance")) / 100) };
				modele.addRow(item);
			}
		}

		resultats.setModel(modele);
		resultats.repaint();
		resultatsScrollPane.repaint();
		this.repaint();

	}
}