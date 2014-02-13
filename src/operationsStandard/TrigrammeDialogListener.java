package operationsStandard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TrigrammeDialogListener implements KeyListener {

	private TrigrammeDialog parent;

	public TrigrammeDialogListener(TrigrammeDialog dialog) {
		super();
		this.parent = dialog;
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
			parent.dispose();
		}
	}

	public void keyReleased(KeyEvent arg0) {
		parent.champSaisie.setText(parent.champSaisie.getText().toUpperCase());
		if (parent.champSaisie.getText().length() == 3) {
			parent.validation = true;
			parent.dispose();
		}
	}

	public void keyTyped(KeyEvent arg0) {
	}

}
