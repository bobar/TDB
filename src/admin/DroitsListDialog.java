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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import main.AuthException;
import main.Droits;
import main.MainWindow;
import main.TDBException;
import main.VerticalTableHeaderCellRenderer;

public class DroitsListDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	protected MainWindow parent;
	private DroitsListDialogListener listener = new DroitsListDialogListener();

	protected JButton creerButton;
	protected JButton modifierButton;
	protected JButton supprimerButton;
	protected JButton fermerButton;

	private JPanel fond;
	protected JTable listeDroits;
	private DefaultTableModel modele = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int rowIndex, int columnInder) {
			return false;
		}

		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0) {
				return String.class;
			} else {
				return Boolean.class;
			}
		}
	};

	private JScrollPane resultatsScrollPane;

	public class DroitsListDialogListener implements ActionListener,
			KeyListener {

		DroitsListDialogListener() {
			super();
		}

		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
				dispose();
			}
		}

		public void keyReleased(KeyEvent arg0) {
		}

		public void keyTyped(KeyEvent arg0) {
		}

		public void actionPerformed(ActionEvent arg0) {
			try {
				if (arg0.getSource().equals(creerButton)) {
					DroitsCreationDialog dialog = new DroitsCreationDialog(
							parent);
					dialog.executer();
					refresh();
				} else if (arg0.getSource().equals(modifierButton)) {
					int ligneChoisie = listeDroits.getSelectedRow();
					if (ligneChoisie == -1) {
						throw new TDBException("Pas de droits sélectionnés");
					}
					int confirmation = JOptionPane.OK_OPTION;
					if (Droits.droitsUtilises(parent,
							(String) listeDroits.getValueAt(ligneChoisie, 0))) {
						confirmation = JOptionPane
								.showConfirmDialog(
										parent,
										"Les droits "
												+ listeDroits.getValueAt(
														ligneChoisie, 0)
												+ " sont actuellement utilisés. Etes-vous sûr de vouloir les modifier ?",
										"Confirmation",
										JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE, null);
					}
					if (confirmation == JOptionPane.OK_OPTION) {
						Droits droits = new Droits(parent,
								(String) listeDroits
										.getValueAt(ligneChoisie, 0));
						DroitsModificationDialog dialog = new DroitsModificationDialog(
								parent, droits);
						dialog.executer();
					}
				} else if (arg0.getSource().equals(supprimerButton)) {
					int ligneChoisie = listeDroits.getSelectedRow();
					if (ligneChoisie == -1) {
						throw new TDBException("Pas de droits sélectionnés");
					}
					int confirmation = JOptionPane.showConfirmDialog(parent,
							"Etes-vous sur de vouloir supprimer les droits "
									+ listeDroits.getValueAt(ligneChoisie, 0),
							"Confirmation", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null);
					if (confirmation == JOptionPane.OK_OPTION) {
						if (!Droits.droitsUtilises(parent, (String) listeDroits
								.getValueAt(ligneChoisie, 0))) {
							Droits droits = new Droits(parent,
									(String) listeDroits.getValueAt(
											ligneChoisie, 0));
							droits.delete();
						} else {
							throw new TDBException("Droits toujours octroyés.");
						}
					}
				} else if (arg0.getSource() == fermerButton) {
					dispose();
				}
				refresh();
			} catch (Exception e) {
				parent.afficherErreur(e);
			}
		}
	}

	public DroitsListDialog(MainWindow parent) {
		super(parent, "Droits", true);
		this.parent = parent;
	}

	public void executer() throws Exception {

		AuthentificationDialog authentification = new AuthentificationDialog(
				parent);
		authentification.executer();

		if (!authentification.admin.has_droit("gestion_admin")) {
			throw new AuthException();
		}

		this.addKeyListener(listener);

		listeDroits = new JTable();
		listeDroits.addKeyListener(listener);
		listeDroits.setModel(modele);
		listeDroits.repaint();

		resultatsScrollPane = new JScrollPane(listeDroits);
		resultatsScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		resultatsScrollPane.setPreferredSize(new Dimension(480, 340));

		String[] header = new String[Droits.nom_droits.length + 1];
		header[0] = "Nom";
		for (int i = 0; i < Droits.nom_droits.length; ++i) {
			header[i + 1] = Droits.nom_droits[i];
		}
		modele.setColumnIdentifiers(header);

		TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
		listeDroits.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listeDroits.setModel(modele);
		listeDroits.getColumnModel().getColumn(0).setPreferredWidth(100);
		for (int i = 0; i < Droits.nom_droits.length; ++i) {
			listeDroits.getColumnModel().getColumn(i + 1)
					.setHeaderRenderer(headerRenderer);
			listeDroits.getColumnModel().getColumn(i + 1).setPreferredWidth(20);
		}
		listeDroits.repaint();

		listeDroits.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		creerButton = new JButton("Créer des droits");
		creerButton.setPreferredSize(new Dimension(220, 20));
		creerButton.addActionListener(listener);

		modifierButton = new JButton("Modifier les droits");
		modifierButton.setPreferredSize(new Dimension(220, 20));
		modifierButton.addActionListener(listener);

		supprimerButton = new JButton("Supprimer les droits");
		supprimerButton.setPreferredSize(new Dimension(220, 20));
		supprimerButton.addActionListener(listener);

		fermerButton = new JButton("Fermer la fenêtre");
		fermerButton.setPreferredSize(new Dimension(220, 20));
		fermerButton.addActionListener(listener);

		fond = new JPanel();
		fond.setLayout(new FlowLayout(FlowLayout.CENTER));
		fond.add(resultatsScrollPane);
		fond.add(creerButton);
		fond.add(modifierButton);
		fond.add(supprimerButton);
		fond.add(fermerButton);

		fond.setPreferredSize(new Dimension(490, 400));
		fond.setOpaque(true);

		this.refresh();
		listeDroits.setModel(modele);
		this.setContentPane(fond);
		this.pack();
		this.setLocation((parent.getWidth() - this.getWidth()) / 2,
				(parent.getHeight() - this.getHeight()) / 2);
		this.setResizable(false);

		this.setVisible(true);
	}

	// On crée une petite fonction refresh pour après les modifs
	public void refresh() throws Exception {
		for (int i = modele.getRowCount() - 1; i >= 0; i--) {
			modele.removeRow(i);
		}
		LinkedList<Droits> droits = Droits.getAllDroits(parent);
		for (Droits droit : droits) {
			Object[] ligne = new Object[1 + Droits.nom_droits.length];
			ligne[0] = droit.nom();
			for (int i = 0; i < Droits.possibilites.length; ++i) {
				ligne[i + 1] = droit.droits().get(Droits.possibilites[i]);
			}
			modele.addRow(ligne);
		}
		listeDroits.setModel(modele);
		listeDroits.repaint();
		resultatsScrollPane.repaint();
		this.repaint();

	}
}