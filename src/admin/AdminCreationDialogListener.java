package admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AdminCreationDialogListener implements KeyListener, ActionListener {

	private AdminCreationDialog parent;

	protected AdminCreationDialogListener(AdminCreationDialog dialog) {
		super();
		this.parent = dialog;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
			parent.validation = true;
			parent.dispose();
		} else if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
			parent.validation = false;
			parent.dispose();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(parent.okButton)) {
			parent.validation = true;
			parent.dispose();
		} else if (e.getSource().equals(parent.cancelButton)) {
			parent.validation = false;
			parent.dispose();
		}

	}

}