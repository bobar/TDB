package admin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

import main.MainWindow;

public class BinetsListeDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	BinetsListeDialogListener listener = new BinetsListeDialogListener(this);

	JButton ouvrirButton;
	JButton fermerButton;

	JPanel fond;
	JTable resultats;
	DefaultTableModel modele = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int rowIndex, int columnInder) {
			return false;
		}
	};
	DefaultTableColumnModel modeleColonnes;
	JScrollPane resultatsScrollPane;

	public BinetsListeDialog(MainWindow parent) {
		super(parent, "Liste des binets", true);
		this.parent = parent;
	}

	public void executer() throws Exception {

		AuthentificationDialog authentification = new AuthentificationDialog(parent);
		authentification.executer();

		if (authentification.droits == AuthentificationDialog.BoBarman) {

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
			ouvrirButton.setPreferredSize(new Dimension(200, 20));
			ouvrirButton.addActionListener(listener);

			fermerButton = new JButton("Fermer la fenêtre");
			fermerButton.setPreferredSize(new Dimension(200, 20));
			fermerButton.addActionListener(listener);

			fond = new JPanel();
			fond.setLayout(new FlowLayout(FlowLayout.CENTER));
			fond.add(resultatsScrollPane);
			fond.add(ouvrirButton);
			fond.add(fermerButton);

			fond.setPreferredSize(new Dimension(500, 500));
			fond.setOpaque(true);

			this.setContentPane(fond);
			this.pack();
			this.setLocation((parent.getWidth() - this.getWidth()) / 2,
					(parent.getHeight() - this.getHeight()) / 2);
			this.chargerListe();
			this.setResizable(false);
			this.setVisible(true);
		}
	}

	// Pour charger la liste, on prend d'abord les binets pas à 0, ceux qui nous
	// intéressent
	void chargerListe() throws Exception {
		for (int i = modele.getRowCount() - 1; i >= 0; i--) {
			modele.removeRow(i);
		}
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT trigramme,name,first_name,balance FROM accounts WHERE status=2 and balance<>0 ORDER BY balance ASC");

		while (rs.next()) {
			String[] item = { rs.getString("trigramme"), rs.getString("name"),
					rs.getString("first_name"), "" + ((double) (rs.getInt("balance")) / 100) };
			modele.addRow(item);
		}
		rs = stmt
				.executeQuery("SELECT trigramme,name,first_name,balance FROM accounts WHERE status=2 and balance=0");

		while (rs.next()) {
			String[] item = { rs.getString("trigramme"), rs.getString("name"),
					rs.getString("first_name"), "" + ((double) (rs.getInt("balance")) / 100) };
			modele.addRow(item);
		}
		resultats.setModel(modele);
		resultats.repaint();
		resultatsScrollPane.repaint();
		this.repaint();

	}

}