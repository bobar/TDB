package operationsGestion;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import main.AuthException;
import main.MainWindow;
import main.Trigramme;
import admin.AuthentificationDialog;

public class TrigrammeCreationDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  MainWindow parent;
  TrigrammeCreationDialogListener listener = new TrigrammeCreationDialogListener();

  JTextField champTrigramme;
  JTextField champNom;
  JTextField champPrenom;
  JTextField champSurnom;
  JTextField champCasert;
  JTextField champSolde;
  JComboBox<String> champCategorie; // 0=X platal,1=X
  // ancien,2=binet,3=personnel,4=autre
  // étudiant,5=autre
  JTextField champPromo;
  JTextField champPhoto;
  JButton photoButton;
  JButton okButton;
  JButton cancelButton;

  boolean validation = false;

  public class TrigrammeCreationDialogListener implements KeyListener, ActionListener {

    public TrigrammeCreationDialogListener() {
      super();
    }

    public void keyPressed(KeyEvent arg0) {
      if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
        validation = true;
        dispose();
      } else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
        validation = false;
        dispose();
      }

    }

    public void keyReleased(KeyEvent arg0) {
      if (arg0.getSource().equals(champTrigramme)) {
        champTrigramme.setText(
            champTrigramme.getText().toUpperCase().substring(0, Math.min(3, champTrigramme.getText().length())));
        if (champTrigramme.getText().length() < 3) {
          champTrigramme.setBackground(null);
        } else {
          try {
            if (!Trigramme.exists(parent, champTrigramme.getText())) {
              champTrigramme.setBackground(Color.GREEN);
            } else {
              champTrigramme.setBackground(Color.RED);
            }

          } catch (Exception e) {
            parent.afficherErreur(e);
          }
        }
        champTrigramme.repaint();
      }
    }

    public void keyTyped(KeyEvent arg0) {}

    public void actionPerformed(ActionEvent e) {
      if (e.getSource().equals(okButton)) {
        validation = true;
        dispose();
      } else if (e.getSource().equals(cancelButton)) {
        validation = false;
        dispose();
      } else if (e.getSource().equals(photoButton)) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {

          public boolean accept(File arg0) {
            return (arg0.isDirectory() || arg0.getName().toLowerCase().contains(".gif")
                || arg0.getName().toLowerCase().contains(".jpg") || arg0.getName().toLowerCase().contains(".jpeg")
                || arg0.getName().toLowerCase().contains(".png"));
          }

          public String getDescription() {
            return null;
          }

        });
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          champPhoto.setText(chooser.getSelectedFile().getAbsolutePath());
        }
      }
    }

  }

  public TrigrammeCreationDialog(MainWindow parent) {
    super(parent, "Création", true);
    this.parent = parent;
  }

  public void executer() throws Exception {

    AuthentificationDialog authentification = new AuthentificationDialog(parent);
    authentification.executer();

    if (!authentification.admin.has_droit("creer_tri")) {
      throw new AuthException();
    }

    JLabel labelTrigramme = new JLabel("Trigramme : ");
    labelTrigramme.setPreferredSize(new Dimension(120, 20));
    labelTrigramme.setHorizontalAlignment(SwingConstants.RIGHT);
    champTrigramme = new JTextField("");
    champTrigramme.setPreferredSize(new Dimension(150, 20));
    champTrigramme.addKeyListener(listener);

    JLabel labelNom = new JLabel("Nom : ");
    labelNom.setPreferredSize(new Dimension(120, 20));
    labelNom.setHorizontalAlignment(SwingConstants.RIGHT);
    champNom = new JTextField("");
    champNom.setPreferredSize(new Dimension(150, 20));
    champNom.addKeyListener(listener);

    JLabel labelPrenom = new JLabel("Prenom : ");
    labelPrenom.setPreferredSize(new Dimension(120, 20));
    labelPrenom.setHorizontalAlignment(SwingConstants.RIGHT);
    champPrenom = new JTextField("");
    champPrenom.setPreferredSize(new Dimension(150, 20));
    champPrenom.addKeyListener(listener);

    JLabel labelSurnom = new JLabel("Surnom : ");
    labelSurnom.setPreferredSize(new Dimension(120, 20));
    labelSurnom.setHorizontalAlignment(SwingConstants.RIGHT);
    champSurnom = new JTextField("");
    champSurnom.setPreferredSize(new Dimension(150, 20));
    champSurnom.addKeyListener(listener);

    JLabel labelCasert = new JLabel("Casert : ");
    labelCasert.setPreferredSize(new Dimension(120, 20));
    labelCasert.setHorizontalAlignment(SwingConstants.RIGHT);
    champCasert = new JTextField("");
    champCasert.setPreferredSize(new Dimension(150, 20));
    champCasert.addKeyListener(listener);

    JLabel labelSolde = new JLabel("Solde : ");
    labelSolde.setPreferredSize(new Dimension(120, 20));
    labelSolde.setHorizontalAlignment(SwingConstants.RIGHT);
    champSolde = new JTextField("");
    champSolde.setPreferredSize(new Dimension(150, 20));
    champSolde.addKeyListener(listener);

    JLabel labelCategorie = new JLabel("Categorie : ");
    labelCategorie.setPreferredSize(new Dimension(120, 20));
    labelCategorie.setHorizontalAlignment(SwingConstants.RIGHT);
    String[] champCategorieValeurs = Trigramme.categoriesList;
    champCategorie = new JComboBox<String>(champCategorieValeurs);
    champCategorie.setPreferredSize(new Dimension(150, 20));
    champCategorie.addKeyListener(listener);

    JLabel labelPromo = new JLabel("Promo : ");
    labelPromo.setPreferredSize(new Dimension(120, 20));
    labelPromo.setHorizontalAlignment(SwingConstants.RIGHT);
    champPromo = new JTextField("");
    champPromo.setPreferredSize(new Dimension(150, 20));
    champPromo.addKeyListener(listener);

    JLabel labelPhoto = new JLabel("Photo : ");
    labelPhoto.setPreferredSize(new Dimension(120, 20));
    labelPhoto.setHorizontalAlignment(SwingConstants.RIGHT);
    champPhoto = new JTextField("");
    champPhoto.setPreferredSize(new Dimension(125, 20));
    champPhoto.addKeyListener(listener);

    photoButton = new JButton("...");
    photoButton.addActionListener(listener);
    photoButton.setPreferredSize(new Dimension(20, 20));

    okButton = new JButton("Valider");
    okButton.addActionListener(listener);
    okButton.setPreferredSize(new Dimension(140, 20));

    cancelButton = new JButton("Annuler");
    cancelButton.addActionListener(listener);
    cancelButton.setPreferredSize(new Dimension(140, 20));

    Properties properties = parent.properties;

    JPanel pane = new JPanel();
    pane.add(labelTrigramme);
    pane.add(champTrigramme);
    if (!properties.getProperty("champNom", "true").equals("false")) {
      pane.add(labelNom);
      pane.add(champNom);
    }
    if (!properties.getProperty("champPrenom", "true").equals("false")) {
      pane.add(labelPrenom);
      pane.add(champPrenom);
    }
    if (!properties.getProperty("champSurnom", "true").equals("false")) {
      pane.add(labelSurnom);
      pane.add(champSurnom);
    }
    if (!properties.getProperty("champCasert", "true").equals("false")) {
      pane.add(labelCasert);
      pane.add(champCasert);
    }
    if (!properties.getProperty("champSolde", "true").equals("false")) {
      pane.add(labelSolde);
      pane.add(champSolde);
    }
    if (!properties.getProperty("champCategorie", "true").equals("false")) {
      pane.add(labelCategorie);
      pane.add(champCategorie);
    }
    if (!properties.getProperty("champPromo", "true").equals("false")) {
      pane.add(labelPromo);
      pane.add(champPromo);
    }
    if (!properties.getProperty("champPhoto", "true").equals("false")) {
      pane.add(labelPhoto);
      pane.add(champPhoto);
      pane.add(photoButton);
    }
    pane.add(okButton);
    pane.add(cancelButton);

    pane.setPreferredSize(new Dimension(300, 260));

    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    Container contentPane = this.getContentPane();
    contentPane.add(pane);
    this.pack();
    this.setLocation((parent.getWidth() - this.getWidth()) / 2, (parent.getHeight() - this.getHeight()) / 2);
    this.setResizable(false);
    this.setVisible(true);

    String nom = champNom.getText().toUpperCase().replace(',', ';');
    String prenom = MainWindow.formatString(champPrenom.getText().toLowerCase());
    if (!prenom.isEmpty())
      prenom = ("" + prenom.charAt(0)).toUpperCase() + prenom.substring(1);
    String surnom = champSurnom.getText().replace(",", ";");

    if (validation && champTrigramme.getBackground().equals(Color.GREEN)) {
      Trigramme trigramme = new Trigramme(parent, champTrigramme.getText(), nom, prenom, surnom, champCasert.getText(),
          champCategorie.getSelectedIndex(), Integer.parseInt("0" + champPromo.getText()), "", champPhoto.getText(),
          (int) (100 * Double.parseDouble("0" + champSolde.getText())),
          (int) (100 * Double.parseDouble("0" + champSolde.getText())));
      trigramme.creer(authentification.admin);
    }
    parent.refresh();
  }
}
