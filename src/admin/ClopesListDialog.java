package admin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
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
import main.AuthException;
import main.Clopes;
import main.MainWindow;
import main.TDBException;

public class ClopesListDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	ClopesListDialogListener listener = new ClopesListDialogListener();

	JButton creerButton;
	JButton modifierButton;
	JButton supprimerButton;
	JButton resetButton;
	JButton fermerButton;

	JPanel fond;
	JTable listeClopes;
	DefaultTableModel modele = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int rowIndex, int columnInder) {
			return false;
		}
	};
	DefaultTableColumnModel modeleColonnes;
	JScrollPane resultatsScrollPane;

	public class ClopesListDialogListener implements ActionListener, KeyListener {

		ClopesListDialogListener() {
			super();
		}

		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
				dispose();
			}
		}

		public void keyReleased(KeyEvent arg0) {}

		public void keyTyped(KeyEvent arg0) {}

		public void actionPerformed(ActionEvent arg0) {
			try {
				if (arg0.getSource().equals(creerButton)) {
					ClopesCreationDialog dialog = new ClopesCreationDialog(parent);
					dialog.executer();
				} else if (arg0.getSource().equals(modifierButton)) {
					int ligneChoisie = listeClopes.getSelectedRow();
					if (ligneChoisie == -1) {
						throw new TDBException("Pas de clopes sélectionnées");
					}
					ClopesModificationDialog dialog =
							new ClopesModificationDialog(parent, (String) listeClopes.getValueAt(
									ligneChoisie, 0));
					dialog.executer();
				} else if (arg0.getSource().equals(supprimerButton)) {
					int ligneChoisie = listeClopes.getSelectedRow();
					if (ligneChoisie == -1) {
						throw new TDBException("Pas de clopes sélectionnées");
					}
					int confirmation =
							JOptionPane.showConfirmDialog(
									parent,
									"Etes-vous sur de vouloir supprimer "
											+ modele.getValueAt(ligneChoisie, 0) + " ?",
									"Confirmation", JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE, null);
					if (confirmation == JOptionPane.OK_OPTION) {
						String marque = (String) modele.getValueAt(ligneChoisie, 0);
						Clopes clopes = new Clopes(parent, marque);
						clopes.delete();
					}
				} else if (arg0.getSource().equals(resetButton)) {
					int confirmation =
							JOptionPane.showConfirmDialog(parent,
									"Etes-vous sur de vouloir remettre les quantités à 0 ?",
									"Confirmation", JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE, null);
					if (confirmation == JOptionPane.OK_OPTION) {
						Clopes.resetQuantites(parent);
					}
				} else if (arg0.getSource().equals(fermerButton)) {
					dispose();
				}
				refresh();
			} catch (Exception e) {
				parent.afficherErreur(e);
			}
		}
	}

	public ClopesListDialog(MainWindow parent) {
		super(parent, "Clopes", true);
		this.parent = parent;
	}

	public void executer() throws Exception {

		AuthentificationDialog authentification = new AuthentificationDialog(parent);
		authentification.executer();

		if (!authentification.admin.has_droit("gestion_clopes")) {
			throw new AuthException();
		}
		this.addKeyListener(listener);

		listeClopes = new JTable();
		listeClopes.addKeyListener(listener);
		listeClopes.setModel(modele);
		listeClopes.repaint();

		resultatsScrollPane = new JScrollPane(listeClopes);
		resultatsScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		resultatsScrollPane.setPreferredSize(new Dimension(330, 320));

		String[] header = { "Marque", "Prix", "Quantité" };
		modele.setColumnIdentifiers(header);

		listeClopes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listeClopes.setModel(modele);
		listeClopes.getColumnModel().getColumn(0).setPreferredWidth(120);
		listeClopes.getColumnModel().getColumn(1).setPreferredWidth(70);
		listeClopes.getColumnModel().getColumn(2).setPreferredWidth(70);
		listeClopes.repaint();

		listeClopes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		creerButton = new JButton("Créer des clopes");
		creerButton.setPreferredSize(new Dimension(220, 20));
		creerButton.addActionListener(listener);

		modifierButton = new JButton("Modifier les clopes");
		modifierButton.setPreferredSize(new Dimension(220, 20));
		modifierButton.addActionListener(listener);

		supprimerButton = new JButton("Supprimer les clopes");
		supprimerButton.setPreferredSize(new Dimension(220, 20));
		supprimerButton.addActionListener(listener);

		resetButton = new JButton("Remettre quantités à 0");
		resetButton.setPreferredSize(new Dimension(220, 20));
		resetButton.addActionListener(listener);

		fermerButton = new JButton("Fermer la fenêtre");
		fermerButton.setPreferredSize(new Dimension(220, 20));
		fermerButton.addActionListener(listener);

		fond = new JPanel();
		fond.setLayout(new FlowLayout(FlowLayout.CENTER));
		fond.add(resultatsScrollPane);
		fond.add(creerButton);
		fond.add(modifierButton);
		fond.add(supprimerButton);
		fond.add(resetButton);
		fond.add(fermerButton);

		fond.setPreferredSize(new Dimension(360, 460));
		fond.setOpaque(true);

		this.refresh();
		listeClopes.setModel(modele);

		this.setContentPane(fond);
		this.pack();
		this.setLocation((parent.getWidth() - this.getWidth()) / 2,
				(parent.getHeight() - this.getHeight()) / 2);
		this.setResizable(false);

		this.setVisible(true);
	}

	public void refresh() throws Exception {
		for (int i = modele.getRowCount() - 1; i >= 0; i--) {
			modele.removeRow(i);
		}
		LinkedList<Clopes> clopes = Clopes.getAllClopes(parent);
		for (Clopes clope : clopes) {
			String[] data =
					{ clope.marque(), "" + (double) clope.prix() / 100, "" + clope.quantite() };
			modele.addRow(data);
		}
		listeClopes.setModel(modele);
		listeClopes.repaint();
		resultatsScrollPane.repaint();
		this.repaint();

	}
}