package admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DebitFichierDialogListener implements ActionListener, KeyListener, MouseListener {

	DebitFichierDialog parent;

	DebitFichierDialogListener(DebitFichierDialog parent) {
		this.parent = parent;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
			parent.dispose();
		} else if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
			// on fait rien exprès pour éviter les fausses manips sordides
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(parent.fermerButton)) {
			parent.validation = false;
			parent.dispose();
		} else if (arg0.getSource().equals(parent.validerButton)) {
			parent.validation = true;
			parent.dispose();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}