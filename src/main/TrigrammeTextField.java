package main;

import java.text.ParseException;

import javax.swing.JFormattedTextField;

public class TrigrammeTextField extends JFormattedTextField {

	private static final long serialVersionUID = 1L;

	public TrigrammeTextField() throws ParseException {
		new JFormattedTextField(new TrigrammeFormatter());
		addKeyListener(new TrigrammeTextFieldListener(this));
	}

}
