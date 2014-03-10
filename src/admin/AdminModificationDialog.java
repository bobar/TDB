package admin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import main.Admin;
import main.MainWindow;
import main.TrigrammeTextField;

public class AdminModificationDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    MainWindow parent;
    AdminModificationDialogListener listener = new AdminModificationDialogListener();
    String trigramme;
    int permissions;
    JFormattedTextField champTrigramme;
    JComboBox<String> champCategorie;
    JButton okButton;
    JButton cancelButton;

    boolean validation = false;

    public class AdminModificationDialogListener implements KeyListener, ActionListener {

	public AdminModificationDialogListener() {
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

	public void keyReleased(KeyEvent arg0) {}

	public void keyTyped(KeyEvent arg0) {}

	public void actionPerformed(ActionEvent e) {
	    if (e.getSource().equals(okButton)) {
		validation = true;
		dispose();
	    } else if (e.getSource().equals(cancelButton)) {
		validation = false;
		dispose();
	    }

	}

    }

    AdminModificationDialog(MainWindow parent, String trigramme, int permissions) {
	super(parent, "Modification d'admin", true);
	this.parent = parent;
	this.trigramme = trigramme;
	this.permissions = permissions;
    }

    public void executer() throws Exception {

	JLabel labelTrigramme = new JLabel("Trigramme : ");
	labelTrigramme.setPreferredSize(new Dimension(120, 20));

	champTrigramme = new TrigrammeTextField();
	champTrigramme.setPreferredSize(new Dimension(150, 20));
	champTrigramme.addKeyListener(listener);
	champTrigramme.setText(trigramme);
	champTrigramme.setEditable(false);

	JLabel labelCategorie = new JLabel("Catégorie : ");
	labelCategorie.setPreferredSize(new Dimension(120, 20));

	champCategorie = new JComboBox<String>(Admin.status_array);
	champCategorie.setPreferredSize(new Dimension(150, 20));
	champCategorie.setSelectedIndex(permissions);
	champCategorie.addKeyListener(listener);

	okButton = new JButton("Valider");
	okButton.addActionListener(listener);
	okButton.setPreferredSize(new Dimension(140, 20));

	cancelButton = new JButton("Annuler");
	cancelButton.addActionListener(listener);
	cancelButton.setPreferredSize(new Dimension(140, 20));

	JPanel pane = new JPanel();
	pane.add(labelTrigramme);
	pane.add(champTrigramme);
	pane.add(labelCategorie);
	pane.add(champCategorie);
	pane.add(okButton);
	pane.add(cancelButton);
	pane.setPreferredSize(new Dimension(300, 80));

	this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	Container contentPane = this.getContentPane();
	contentPane.add(pane);
	this.pack();
	this.setLocation((parent.getWidth() - this.getWidth()) / 2,
		(parent.getHeight() - this.getHeight()) / 2);
	this.setResizable(false);
	this.setVisible(true);

	if (validation) {
	    try {
		Admin admin = new Admin(parent, champTrigramme.getText());
		admin.setPerms(champCategorie.getSelectedIndex());
	    } catch (Exception e) {
		parent.afficherErreur(e);
	    }
	}
    }

}