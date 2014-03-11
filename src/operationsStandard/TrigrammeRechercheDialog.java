package operationsStandard;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
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
import main.Trigramme;

public class TrigrammeRechercheDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    MainWindow parent;
    TrigrammeRechercheDialogListener listener = new TrigrammeRechercheDialogListener();

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

    public class TrigrammeRechercheDialogListener implements ActionListener, KeyListener,
	    MouseListener {

	TrigrammeRechercheDialogListener() {}

	public void keyPressed(KeyEvent arg0) {
	    if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
		dispose();
	    } else if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
		try {
		    chargerListe();
		} catch (Exception e) {
		    parent.afficherErreur(e);
		}
	    }
	}

	public void keyReleased(KeyEvent arg0) {
	    try {
		chargerListe();
	    } catch (Exception e) {
		parent.afficherErreur(e);
	    }
	}

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
	    } else if (arg0.getSource() == rechercheButton) {
		try {
		    chargerListe();
		} catch (Exception e) {
		    parent.afficherErreur(e);
		}
	    }
	}

	public void mouseClicked(MouseEvent arg0) {
	    if (arg0.getClickCount() == 2) {
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

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}
    }

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
	resultatsScrollPane.setPreferredSize(new Dimension(390, 340));

	String[] header = { "Trigramme", "Nom", "Prénom", "Surnom" };
	modele.setColumnIdentifiers(header);

	resultats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	resultats.setModel(modele);
	resultats.getColumnModel().getColumn(0).setPreferredWidth(65);
	resultats.getColumnModel().getColumn(1).setPreferredWidth(70);
	resultats.getColumnModel().getColumn(2).setPreferredWidth(60);
	resultats.getColumnModel().getColumn(3).setPreferredWidth(80);
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

	fond.setPreferredSize(new Dimension(400, 450));
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
	Vector<Trigramme> trigrammes =
		Trigramme.rechercher(parent, champSaisie.getText());
	for (int i = 0; i < trigrammes.size(); ++i) {
	    String[] ligne =
		    { trigrammes.get(i).trigramme, trigrammes.get(i).name,
			    trigrammes.get(i).first_name, trigrammes.get(i).nickname };
	    modele.addRow(ligne);
	}
	resultats.setModel(modele);
	resultats.repaint();
	resultatsScrollPane.repaint();
	this.repaint();

    }
}
