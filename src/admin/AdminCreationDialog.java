package admin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.WindowConstants;
import main.Admin;
import main.MainWindow;
import main.TrigrammeTextField;

public class AdminCreationDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    protected MainWindow parent;
    private AdminCreationDialogListener listener = new AdminCreationDialogListener();

    private JFormattedTextField champTrigramme;
    private JComboBox<String> champCategorie;
    private JPasswordField champMDP1;
    private JPasswordField champMDP2;
    protected JButton okButton;
    protected JButton cancelButton;

    protected boolean validation = false;

    public class AdminCreationDialogListener implements KeyListener, ActionListener {

	protected AdminCreationDialogListener() {
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

    protected AdminCreationDialog(MainWindow parent) {
	super(parent, "Création d'admin", true);
	this.parent = parent;
    }

    protected void executer() throws Exception {

	JLabel labelTrigramme = new JLabel("Trigramme : ");
	labelTrigramme.setPreferredSize(new Dimension(120, 20));

	champTrigramme = new TrigrammeTextField();
	champTrigramme.setPreferredSize(new Dimension(150, 20));
	champTrigramme.addKeyListener(listener);

	JLabel labelCategorie = new JLabel("Catégorie : ");
	labelCategorie.setPreferredSize(new Dimension(120, 20));

	champCategorie = new JComboBox<String>(Admin.status_array);
	champCategorie.setPreferredSize(new Dimension(150, 20));
	champCategorie.addKeyListener(listener);

	JLabel labelMDP1 = new JLabel("Mot de passe : ");
	labelMDP1.setPreferredSize(new Dimension(120, 20));

	champMDP1 = new JPasswordField();
	champMDP1.setPreferredSize(new Dimension(150, 20));
	champMDP1.addKeyListener(listener);

	JLabel labelMDP2 = new JLabel("Confirmez : ");
	labelMDP2.setPreferredSize(new Dimension(120, 20));

	champMDP2 = new JPasswordField();
	champMDP2.setPreferredSize(new Dimension(150, 20));
	champMDP2.addKeyListener(listener);

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
	pane.add(labelMDP1);
	pane.add(champMDP1);
	pane.add(labelMDP2);
	pane.add(champMDP2);
	pane.add(okButton);
	pane.add(cancelButton);
	pane.setPreferredSize(new Dimension(300, 130));

	this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	Container contentPane = this.getContentPane();
	contentPane.add(pane);
	this.pack();
	this.setLocation((parent.getWidth() - this.getWidth()) / 2,
		(parent.getHeight() - this.getHeight()) / 2);
	this.setResizable(false);
	this.setVisible(true);

	if (validation) {
	    String cryptage1 = MD5Hex(champMDP1.getPassword());
	    String cryptage2 = MD5Hex(champMDP2.getPassword());
	    if (!cryptage1.equals(cryptage2)) { throw new Exception(
		    "Les mots de passe ne correspondent pas"); }
	    try {
		Admin new_admin = new Admin(parent,champTrigramme.getText(),champCategorie.getSelectedIndex(),cryptage1);
		new_admin.creer();
	    } catch (Exception e) {
		parent.afficherErreur(e);
	    }
	}
    }

    private static String MD5Hex(String s) {
	String result = null;
	try {
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	    byte[] digest = md5.digest(s.getBytes());
	    result = toHex(digest);
	} catch (NoSuchAlgorithmException e) {
	    // this won't happen, we know Java has MD5!
	}
	return result;
    }

    public static String MD5Hex(char[] c) {
	String init = "";
	for (int i = c.length - 1; i >= 0; --i) {
	    init = c[i] + init;
	}
	return MD5Hex(init);
    }

    private static String toHex(byte[] a) {
	StringBuilder sb = new StringBuilder(a.length * 2);
	for (int i = 0; i < a.length; i++) {
	    sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
	    sb.append(Character.forDigit(a[i] & 0x0f, 16));
	}
	return sb.toString();
    }
}