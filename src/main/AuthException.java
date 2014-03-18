package main;

public class AuthException extends TDBException {

	private static final long serialVersionUID = 1L;

	public AuthException(String s) {
		super(s);
	}

	public AuthException() {
		super("Vous n'avez pas les droits nécessaires");
	}

}