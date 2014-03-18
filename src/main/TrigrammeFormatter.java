package main;

import java.text.ParseException;
import javax.swing.text.MaskFormatter;

public class TrigrammeFormatter extends MaskFormatter {

	private static final long serialVersionUID = 1L;

	public TrigrammeFormatter() throws ParseException {
		super("****");
		setValidCharacters("abcdefghijklmnopqrstuvwxyzàäâéèëêïîöôùüûÿŷABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/*-+");
	}
}
