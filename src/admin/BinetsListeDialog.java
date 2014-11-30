package admin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import main.AuthException;
import main.MainWindow;
import main.Trigramme;

public class BinetsListeDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  MainWindow parent;
  BinetsListeDialogListener listener = new BinetsListeDialogListener();

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

  public class BinetsListeDialogListener implements ActionListener, KeyListener {

    BinetsListeDialogListener() {
      super();
    }

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
      } else if (arg0.getSource() == ouvrirButton) {
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
  }

  public BinetsListeDialog(MainWindow parent) {
    super(parent, "Liste des binets", true);
    this.parent = parent;
  }

  public void executer() throws Exception {

    AuthentificationDialog authentification = new AuthentificationDialog(parent);
    authentification.executer();

    if (!authentification.admin.has_droit("voir_comptes")) {
      throw new AuthException();
    }

    this.addKeyListener(listener);

    resultats = new JTable();
    resultats.addKeyListener(listener);
    resultats.setModel(modele);
    resultats.repaint();

    resultatsScrollPane = new JScrollPane(resultats);
    resultatsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    resultatsScrollPane.setPreferredSize(new Dimension(450, 460));

    String[] header = {"Trigramme", "Nom", "Prénom", "Balance"};
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

  // Pour charger la liste, on prend d'abord les binets pas à 0, ceux qui nous
  // intéressent
  void chargerListe() throws Exception {
    for (int i = modele.getRowCount() - 1; i >= 0; i--) {
      modele.removeRow(i);
    }
    Statement stmt = parent.connexion.createStatement();

    ResultSet rs =
        stmt.executeQuery("SELECT trigramme,name,first_name,balance FROM accounts WHERE status=2 and balance<>0 ORDER BY balance ASC");
    while (rs.next()) {
      String[] item =
          {rs.getString("trigramme"), rs.getString("name"), rs.getString("first_name"),
              "" + ((double) (rs.getInt("balance")) / 100)};
      modele.addRow(item);
    }
    rs =
        stmt.executeQuery("SELECT trigramme,name,first_name,balance FROM accounts WHERE status=2 and balance=0");
    while (rs.next()) {
      String[] item =
          {rs.getString("trigramme"), rs.getString("name"), rs.getString("first_name"),
              "" + ((double) (rs.getInt("balance")) / 100)};
      modele.addRow(item);
    }
    resultats.setModel(modele);
    resultats.repaint();
    resultatsScrollPane.repaint();
    this.repaint();

  }

}
