package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import javax.swing.JFormattedTextField;

public class TrigrammeTextField extends JFormattedTextField {

    private static final long serialVersionUID = 1L;

    public class TrigrammeTextFieldListener implements KeyListener {

	public TrigrammeTextFieldListener(TrigrammeTextField parent) {
	    super();
	}

	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {
	    String avant = getText().substring(0, Math.min(3, getText().length()));
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
	    setText(formatage);
	}

	public void keyTyped(KeyEvent e) {}

    }

    public TrigrammeTextField() throws ParseException {
	new JFormattedTextField(new TrigrammeFormatter());
	addKeyListener(new TrigrammeTextFieldListener(this));
    }

}
