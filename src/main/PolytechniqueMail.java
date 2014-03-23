package main;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PolytechniqueMail {
	private PolytechniqueMail() {}

	public static void Send(final String username, final String password, String recipientEmail,
			String title, String message) throws AddressException, MessagingException {
		PolytechniqueMail.Send(username, password, recipientEmail, "", title, message);
	}

	public static void Send(final String username, final String password, String recipientEmail,
			String ccEmail, String title, String message) throws AddressException,
			MessagingException {
		//Set the host smtp address
	    Properties props = new Properties();
	    props.put("mail.smtp.host", "mx.polytechnique.fr");

	    // create some properties and get the default Session
	    Session session = Session.getDefaultInstance(props, null);
	    session.setDebug(false);

	    // create a message
	    Message msg = new MimeMessage(session);

	    // set the from and to address
	    InternetAddress addressFrom = new InternetAddress("bobar@binets.polytechnique.fr");
	    msg.setFrom(addressFrom);

	    InternetAddress destinataire = new InternetAddress(recipientEmail);
	    msg.addRecipient(Message.RecipientType.TO, destinataire);

	    // Setting the Subject and Content Type
	    msg.setSubject(title);
		msg.setContent(message, "text/plain");
	    Transport.send(msg);
	}
}