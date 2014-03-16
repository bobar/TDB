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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.WindowConstants;
import main.MainWindow;
import main.TDBException;

// Pour info : 0=pékin, 1=ami du BôB, 2=ex-BôBarman, 3=BôBarman

public class MotDePasseDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    MainWindow parent;
    MotDePasseDialogListener listener = new MotDePasseDialogListener();

    JPasswordField champMDP1;
    JPasswordField champMDP2;
    JButton okButton;
    JButton cancelButton;

    int admin = 0;
    int droits = 0;
    boolean validation = false;

    public class MotDePasseDialogListener implements KeyListener, ActionListener {

	public MotDePasseDialogListener() {
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

    public MotDePasseDialog(MainWindow parent) {
	super(parent, "Nouveau mot de passe", true);
	this.parent = parent;
    }

    public void executer() throws Exception {

	AuthentificationDialog authentification = new AuthentificationDialog(parent);
	authentification.executer();
	if (authentification.admin.pekin()) {
	    JLabel labelMDP1 = new JLabel("Mot de passe : ");
	    labelMDP1.setPreferredSize(new Dimension(120, 20));
	    champMDP1 = new JPasswordField();
	    champMDP1.setPreferredSize(new Dimension(150, 20));

	    JLabel labelMDP2 = new JLabel("Confirmation : ");
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
	    pane.add(labelMDP1);
	    pane.add(champMDP1);
	    pane.add(labelMDP2);
	    pane.add(champMDP2);
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

	    if (validation = true) {
		String mdp1 = MD5Hex(champMDP1.getPassword());
		String mdp2 = MD5Hex(champMDP2.getPassword());
		if (!mdp1.equals(mdp2)) {
		    throw new TDBException("Les mots de passe ne correspondent pas");
		} else {
		    authentification.admin.setPasswd(mdp1);
		    JOptionPane.showMessageDialog(this, "Mot de passe changé", "",
			    JOptionPane.INFORMATION_MESSAGE);
		}
	    }
	} else {
	    throw new TDBException("Vous n'avez pas de compte admin");
	}
    }

    public static String MD5Hex(String s) {
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

    public static String toHex(byte[] a) {
	StringBuilder sb = new StringBuilder(a.length * 2);
	for (int i = 0; i < a.length; i++) {
	    sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
	    sb.append(Character.forDigit(a[i] & 0x0f, 16));
	}
	return sb.toString();
    }
}