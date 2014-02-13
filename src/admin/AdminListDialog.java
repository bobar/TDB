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
import javax.swing.table.DefaultTableModel;

import main.MainWindow;
import main.Trigramme;

public class AdminListDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	protected MainWindow parent;
	private AdminListDialogListener listener = new AdminListDialogListener(this);

	protected JButton creerButton;
	protected JButton modifierButton;
	protected JButton supprimerButton;
	protected JButton fermerButton;

	private JPanel fond;
	protected JTable listeAdmin;
	private DefaultTableModel modele = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int rowIndex, int columnInder) {
			return false;
		}
	};
	private JScrollPane resultatsScrollPane;

	public AdminListDialog(MainWindow parent) {
		super(parent, "Administrateurs", true);
		this.parent = parent;
	}

	public void executer() throws Exception {

		AuthentificationDialog authentification = new AuthentificationDialog(parent);
		authentification.executer();

		if (authentification.droits == AuthentificationDialog.BoBarman) {
			this.addKeyListener(listener);

			listeAdmin = new JTable();
			listeAdmin.addKeyListener(listener);
			listeAdmin.setModel(modele);
			listeAdmin.repaint();

			resultatsScrollPane = new JScrollPane(listeAdmin);
			resultatsScrollPane
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			resultatsScrollPane.setPreferredSize(new Dimension(400, 340));

			String[] header = { "Trigramme", "Nom", "Prénom", "Statut" };
			modele.setColumnIdentifiers(header);

			listeAdmin.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listeAdmin.setModel(modele);
			listeAdmin.getColumnModel().getColumn(0).setPreferredWidth(65);
			listeAdmin.getColumnModel().getColumn(1).setPreferredWidth(80);
			listeAdmin.getColumnModel().getColumn(2).setPreferredWidth(80);
			listeAdmin.getColumnModel().getColumn(3).setPreferredWidth(70);
			listeAdmin.repaint();

			listeAdmin.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			creerButton = new JButton("Créer un admin");
			creerButton.setPreferredSize(new Dimension(220, 20));
			creerButton.addActionListener(listener);

			modifierButton = new JButton("Modifier l'admin");
			modifierButton.setPreferredSize(new Dimension(220, 20));
			modifierButton.addActionListener(listener);

			supprimerButton = new JButton("Supprimer l'admin");
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

			fond.setPreferredSize(new Dimension(410, 450));
			fond.setOpaque(true);

			Statement stmt = parent.connexion.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT admins.id,permissions,trigramme,name,first_name FROM admins INNER JOIN accounts ON admins.id=accounts.id ORDER BY permissions DESC,name ASC");

			while (rs.next()) {
				String perms = Trigramme.adminCategoriesList[rs.getInt("permissions")];
				String[] item = { rs.getString("trigramme"), rs.getString("name"),
						rs.getString("first_name"), perms };
				modele.addRow(item);
			}
			listeAdmin.setModel(modele);

			this.setContentPane(fond);
			this.pack();
			this.setLocation((parent.getWidth() - this.getWidth()) / 2,
					(parent.getHeight() - this.getHeight()) / 2);
			this.setResizable(false);

			this.setVisible(true);
		}
	}

	// On crée une petite fonction refresh pour après les modifs
	public void refresh() throws Exception {
		for (int i = modele.getRowCount() - 1; i >= 0; i--) {
			modele.removeRow(i);
		}
		Statement stmt = parent.connexion.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT admins.id,permissions,trigramme,name,first_name FROM admins INNER JOIN accounts ON admins.id=accounts.id ORDER BY permissions DESC,name ASC");

		while (rs.next()) {
			String perms = Trigramme.adminCategoriesList[rs.getInt("permissions")];
			String[] item = { rs.getString("trigramme"), rs.getString("name"),
					rs.getString("first_name"), perms };
			modele.addRow(item);
		}
		listeAdmin.setModel(modele);
		listeAdmin.repaint();
		resultatsScrollPane.repaint();
		this.repaint();

	}
}