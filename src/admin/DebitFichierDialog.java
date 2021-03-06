package admin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

import main.Admin;
import main.AuthException;
import main.MainWindow;
import main.Transaction;
import main.TrigException;
import main.Trigramme;
import operationsStandard.TrigrammeDialog;

public class DebitFichierDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  MainWindow parent;
  DebitFichierDialogListener listener = new DebitFichierDialogListener();

  JButton validerButton;
  JButton fermerButton;

  LinkedList<Integer> lignesInterdites;

  JPanel fond;
  JTable resultats;
  DefaultTableModel modele = new DefaultTableModel() {
    private static final long serialVersionUID = 1L;

    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return (columnIndex == 0 && !lignesInterdites.contains(rowIndex));
    }

    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 0) {
        return Boolean.class;
      } else {
        return String.class;
      }
    }
  };
  DefaultTableColumnModel modeleColonnes;
  JScrollPane resultatsScrollPane;
  boolean validation;

  public class DebitFichierDialogListener implements ActionListener, KeyListener, MouseListener {

    DebitFichierDialogListener() {}

    public void keyPressed(KeyEvent arg0) {
      if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
        dispose();
      } else if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
        // on fait rien exprès pour éviter les fausses manips sordides
      }
    }

    public void keyReleased(KeyEvent arg0) {}

    public void keyTyped(KeyEvent arg0) {}

    public void actionPerformed(ActionEvent arg0) {
      if (arg0.getSource().equals(fermerButton)) {
        validation = false;
        dispose();
      } else if (arg0.getSource().equals(validerButton)) {
        validation = true;
        dispose();
      }
    }

    public void mouseClicked(MouseEvent arg0) {}

    public void mouseEntered(MouseEvent arg0) {}

    public void mouseExited(MouseEvent arg0) {}

    public void mousePressed(MouseEvent arg0) {}

    public void mouseReleased(MouseEvent arg0) {}
  }

  public DebitFichierDialog(MainWindow parent) {
    super(parent, "Débit depuis fichier", true);
    this.parent = parent;
  }

  class csvFileFilter extends FileFilter {

    public boolean accept(File arg0) {
      return (arg0.isDirectory() || arg0.getName().contains(".csv"));
    }

    public String getDescription() {
      return null;
    }

  }

  public static int Hamming(String a, String b) {
    a = Normalizer.normalize(a.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    b = Normalizer.normalize(b.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    int hamming = Math.abs(a.length() - b.length());
    for (int i = 0; i < Math.min(a.length(), b.length()); ++i) {
      if (a.charAt(i) != b.charAt(i)) {
        ++hamming;
      }
    }
    return hamming;
  }

  public void executer() throws Exception {

    if (parent.trigrammeActif == null || parent.trigrammeActif.status != Trigramme.Binet) {
      TrigrammeDialog dialog = new TrigrammeDialog(parent, "");
      dialog.executer();
    }
    if (parent.trigrammeActif == null) {
      throw new TrigException("Pas de trigramme banque ouvert");
    } else if (parent.trigrammeActif.status != Trigramme.Binet) {
      throw new TrigException("Le trigramme n'est pas un compte binet");
    }

    AuthentificationDialog authentification = new AuthentificationDialog(parent);
    authentification.executer();
    if (!authentification.admin.has_droit("debit_fichier")) {
      throw new AuthException();
    }
    int banqueId = parent.trigrammeActif.id;
    Admin adminId = authentification.admin;

    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new csvFileFilter());
    int returnVal = chooser.showOpenDialog(parent);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String fichier = chooser.getSelectedFile().getAbsolutePath();

      this.addKeyListener(listener);

      resultats = new JTable();
      resultats.addKeyListener(listener);
      resultats.setModel(modele);

      resultatsScrollPane = new JScrollPane(resultats);
      resultatsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      resultatsScrollPane.setPreferredSize(new Dimension(590, 560));

      String[] header = {"", "Trigramme", "Nom", "Montant", "Commentaire"};
      modele.setColumnIdentifiers(header);

      resultats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      resultats.getColumnModel().getColumn(0).setPreferredWidth(15);
      resultats.getColumnModel().getColumn(1).setPreferredWidth(50);
      resultats.getColumnModel().getColumn(2).setPreferredWidth(100);
      resultats.getColumnModel().getColumn(3).setPreferredWidth(30);
      resultats.getColumnModel().getColumn(4).setPreferredWidth(250);

      InputStream ips = new FileInputStream(fichier);
      InputStreamReader ipsr = new InputStreamReader(ips);
      BufferedReader br = new BufferedReader(ipsr);
      String ligne;

      while ((ligne = br.readLine()) != null) {
        char separateur = ',';
        int virgules = MainWindow.countOccurrences(ligne, ',');
        int pointvirgules = MainWindow.countOccurrences(ligne, ';');
        int deuxpoints = MainWindow.countOccurrences(ligne, ':');
        int espaces = MainWindow.countOccurrences(ligne, ' ');
        int tabs = MainWindow.countOccurrences(ligne, '\t');

        int best =
            Math.max(Math.max(virgules, pointvirgules),
                Math.max(deuxpoints, Math.max(espaces, tabs)));
        if (best == virgules) {
          separateur = ',';
        } else if (best == pointvirgules) {
          separateur = ';';
        } else if (best == deuxpoints) {
          separateur = ':';
        } else if (best == espaces) {
          separateur = ' ';
        } else if (best == tabs) {
          separateur = '\t';
        }

        String[] valeurs = ligne.split(separateur + "");
        if (valeurs.length == 4) {
          valeurs[2] = valeurs[2] + '.' + valeurs[3];
        }
        if (valeurs.length == 3) {
          Object[] futureLigne = {true, valeurs[0], valeurs[1], valeurs[2].replace(',', '.'), ""};
          modele.addRow(futureLigne);
        } else {
          Object[] futureLigne = {false, "", "Illisible", "", ligne};
          modele.addRow(futureLigne);
        }

      }
      br.close();

      lignesInterdites = new LinkedList<Integer>();

      for (int i = 0; i < modele.getRowCount(); i++) {
        int erreur = -1;
        String comment = "";
        try {
          Trigramme trigramme = new Trigramme(parent, (String) modele.getValueAt(i, 1));
          int montant = (int) (100 * Double.parseDouble((String) modele.getValueAt(i, 3)));
          int hamming = Hamming(trigramme.name, modele.getValueAt(i, 2).toString());
          if (montant < 0) {
            lignesInterdites.add(i);
            erreur = Math.max(erreur, 2);
            comment = "Montant négatif";
          } else if (trigramme.status != 0 && trigramme.balance < montant) {
            erreur = Math.max(erreur, 2);
            comment =
                "Compte non X passe en négatif (" + (double) (trigramme.balance - montant) / 100
                    + ")";
          } else if (hamming >= 2) {
            erreur = Math.max(erreur, 2);
            comment = "Nom différent : " + trigramme.name;
          } else if (montant > 2000) {
            erreur = Math.max(erreur, 1);
            comment = "Montant élevé";
          } else if (trigramme.balance < montant && trigramme.balance >= 0) {
            erreur = Math.max(erreur, 1);
            comment =
                "Le compte passe en négatif (" + (double) (trigramme.balance - montant) / 100 + ")";
          } else if (hamming == 1) {
            erreur = Math.max(erreur, 1);
            comment = "Légère erreur dans le nom : " + trigramme.name;
          }
          modele.setValueAt(comment, i, 4);
          if (erreur == 2) {
            modele.setValueAt(Boolean.FALSE, i, 0);
          }

        } catch (Exception e) {
          comment = "Trigramme inexistant";
          lignesInterdites.add(i);
          modele.setValueAt(Boolean.FALSE, i, 0);
          modele.setValueAt(comment, i, 4);
        }

      }

      resultats.setModel(modele);
      resultats.repaint();
      resultatsScrollPane.repaint();
      this.repaint();

      validerButton = new JButton("Valider");
      validerButton.setPreferredSize(new Dimension(200, 20));
      validerButton.addActionListener(listener);

      fermerButton = new JButton("Fermer la fenêtre");
      fermerButton.setPreferredSize(new Dimension(200, 20));
      fermerButton.addActionListener(listener);

      fond = new JPanel();
      fond.setLayout(new FlowLayout(FlowLayout.CENTER));
      fond.add(resultatsScrollPane);
      fond.add(validerButton);
      fond.add(fermerButton);

      fond.setPreferredSize(new Dimension(600, 600));
      fond.setOpaque(true);

      this.setContentPane(fond);
      this.pack();
      this.setLocation((parent.getWidth() - this.getWidth()) / 2,
          (parent.getHeight() - this.getHeight()) / 2);
      this.setResizable(false);
      this.setVisible(true);

      if (validation) {

        PrintWriter out =
            new PrintWriter(new BufferedWriter(new FileWriter(fichier.replace(".csv", "")
                + "_refuses.csv", true)));
        int refuses = 0;

        for (int i = 0; i < modele.getRowCount(); i++) {

          if (modele.getValueAt(i, 0).equals(Boolean.TRUE)) {
            int montant = (int) (100 * Double.parseDouble((String) modele.getValueAt(i, 3)));
            Trigramme trigramme = new Trigramme(parent, (String) modele.getValueAt(i, 1));

            if (!parent.banqueBobActif) {
              banqueId = parent.banqueBinet.id;
            }
            Transaction transaction =
                new Transaction(trigramme.id, montant, "", adminId, null, banqueId);
            transaction.WriteToDB(parent);
          } else {
            out.println(modele.getValueAt(i, 1) + "," + modele.getValueAt(i, 2) + ","
                + modele.getValueAt(i, 3) + "," + modele.getValueAt(i, 4));
            refuses++;
          }

        }
        out.close();

        if (refuses == 0) {
          JOptionPane.showMessageDialog(parent, "Opération terminéé", "",
              JOptionPane.INFORMATION_MESSAGE, null);
        } else {
          JOptionPane.showMessageDialog(parent, "Les " + refuses
              + " lignes ignorées ont été stockées dans " + fichier.replace(".csv", "")
              + "_refuses.csv", "", JOptionPane.INFORMATION_MESSAGE, null);
        }

        parent.refresh();

      }

    }
  }
}
