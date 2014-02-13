package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TrigrammeTextFieldListener implements KeyListener {

	private TrigrammeTextField parent;

	public TrigrammeTextFieldListener(TrigrammeTextField parent) {
		super();
		this.parent = parent;
	}

	public void keyPressed(KeyEvent e) {
		// String avant = parent.getText().substring(0, Math.min(3,
		// parent.getText().length()));
		// String formatage = "";
		// for (int i = 0; i < avant.length(); i++) {
		// char a = 'a';
		// if (a == 'à' || a == 'ä' || a == 'â') {
		// formatage += 'A';
		// } else if (a == 'é' || a == 'è' || a == 'ë' || a == 'ê') {
		// formatage += 'E';
		// } else if (a == 'ï' || a == 'î') {
		// formatage += 'I';
		// } else if (a == 'ö' || a == 'ô') {
		// formatage += 'O';
		// } else if (a == 'ü' || a == 'û' || a == 'ù') {
		// formatage += 'U';
		// } else if (a == 'ÿ' || a == 'ŷ') {
		// formatage += 'Y';
		// } else {
		// formatage += ("" + a).toUpperCase();
		// }
		// }
		// parent.setText(formatage);
	}

	public void keyReleased(KeyEvent e) {
		String avant = parent.getText().substring(0, Math.min(3, parent.getText().length()));
		String formatage = "";
		for (int i = 0; i < avant.length(); i++) {
			char a = avant.charAt(i);
			if (a == 'à' || a == 'ä' || a == 'â') {
				formatage += 'A';
			} else if (a == 'é' || a == 'è' || a == 'ë' || a == 'ê') {
				formatage += 'E';
			} else if (a == 'ï' || a == 'î') {
				formatage += 'I';
			} else if (a == 'ö' || a == 'ô') {
				formatage += 'O';
			} else if (a == 'ü' || a == 'û' || a == 'ù') {
				formatage += 'U';
			} else if (a == 'ÿ' || a == 'ŷ') {
				formatage += 'Y';
			} else {
				formatage += ("" + a).toUpperCase();
			}
		}
		parent.setText(formatage);
	}

	public void keyTyped(KeyEvent e) {
	}

}
