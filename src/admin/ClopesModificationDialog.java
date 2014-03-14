package admin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import main.Clopes;
import main.MainWindow;

public class ClopesModificationDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    MainWindow parent;
    ClopesModificationDialogListener listener = new ClopesModificationDialogListener();
    Clopes clope;
    JTextField champMarque;
    JTextField champPrix;
    JButton okButton;
    JButton cancelButton;

    boolean validation = false;

    public class ClopesModificationDialogListener implements KeyListener, ActionListener {

	public ClopesModificationDialogListener() {
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

    ClopesModificationDialog(MainWindow parent, String marque) throws Exception {
	super(parent, "Modifier des clopes", true);
	this.parent = parent;
	this.clope = new Clopes(parent, marque);
    }

    public void executer() throws Exception {

	JLabel labelTrigramme = new JLabel("Marque : ");
	labelTrigramme.setPreferredSize(new Dimension(120, 20));

	champMarque = new JTextField();
	champMarque.setPreferredSize(new Dimension(150, 20));
	champMarque.addKeyListener(listener);
	champMarque.setText(clope.marque());
	champMarque.setEditable(false);

	JLabel labelPrix = new JLabel("Prix : ");
	labelPrix.setPreferredSize(new Dimension(120, 20));

	champPrix = new JTextField();
	champPrix.setPreferredSize(new Dimension(150, 20));
	champPrix.setText((double) clope.prix() / 100 + "");
	champPrix.addKeyListener(listener);

	okButton = new JButton("Valider");
	okButton.addActionListener(listener);
	okButton.setPreferredSize(new Dimension(140, 20));

	cancelButton = new JButton("Annuler");
	cancelButton.addActionListener(listener);
	cancelButton.setPreferredSize(new Dimension(140, 20));

	JPanel pane = new JPanel();
	pane.add(labelTrigramme);
	pane.add(champMarque);
	pane.add(labelPrix);
	pane.add(champPrix);
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
		clope.setPrix((int) Math.round(100 * Double.parseDouble(champPrix.getText())));
	    } catch (Exception e) {
		parent.afficherErreur(e);
	    }
	}
    }
}