package operationsStandard;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

import main.MainWindow;

public class TrigrammeRechercheDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	MainWindow parent;
	TrigrammeRechercheDialogListener listener = new TrigrammeRechercheDialogListener(this);

	JTextField champSaisie;
	JButton rechercheButton;
	JButton ouvrirButton;
	JButton fermerButton;

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

	public TrigrammeRechercheDialog(MainWindow parent) {
		super(parent, "Rechercher quelqu'un", true);
		this.parent = parent;
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
		resultatsScrollPane.setPreferredSize(new Dimension(290, 340));

		String[] header = { "Trigramme", "Nom", "Prénom" };
		modele.setColumnIdentifiers(header);

		resultats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultats.setModel(modele);
		resultats.getColumnModel().getColumn(0).setPreferredWidth(65);
		resultats.getColumnModel().getColumn(1).setPreferredWidth(60);
		resultats.getColumnModel().getColumn(2).setPreferredWidth(50);
		resultats.addMouseListener(listener);
		resultats.repaint();

		resultats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		champSaisie = new JTextField();
		champSaisie.addKeyListener(listener);
		champSaisie.setPreferredSize(new Dimension(200, 20));

		rechercheButton = new JButton("Recherche");
		rechercheButton.setPreferredSize(new Dimension(200, 20));
		rechercheButton.addActionListener(listener);

		ouvrirButton = new JButton("Ouvrir le trigramme");
		ouvrirButton.setPreferredSize(new Dimension(200, 20));
		ouvrirButton.addActionListener(listener);

		fermerButton = new JButton("Fermer la fenêtre");
		fermerButton.setPreferredSize(new Dimension(200, 20));
		fermerButton.addActionListener(listener);

		fond = new JPanel();
		fond.setLayout(new FlowLayout(FlowLayout.CENTER));
		fond.add(champSaisie);
		fond.add(resultatsScrollPane);
		fond.add(rechercheButton);
		fond.add(ouvrirButton);
		fond.add(fermerButton);

		fond.setPreferredSize(new Dimension(300, 450));
		fond.setOpaque(true);

		this.setContentPane(fond);
		this.pack();
		this.setLocation((parent.getWidth() - this.getWidth()) / 2,
				(parent.getHeight() - this.getHeight()) / 2);
		this.setResizable(false);
		this.setVisible(true);
	}

	void chargerListe() throws Exception {
		for (int i = modele.getRowCount() - 1; i >= 0; i--) {
			modele.removeRow(i);
		}
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT trigramme,name,first_name FROM accounts WHERE name LIKE '%"
						+ champSaisie.getText() + "%' OR first_name LIKE '%"
						+ champSaisie.getText() + "%'");

		while (rs.next()) {
			String[] item = { rs.getString("trigramme"), rs.getString("name"),
					rs.getString("first_name") };
			modele.addRow(item);
		}
		resultats.setModel(modele);
		resultats.repaint();
		resultatsScrollPane.repaint();
		this.repaint();

	}
}
