/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DDosClientPackage;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMailUsingAttachmentSMTP2 {
	public String SMTP_HOST_NAME = "smtp.net4india.com";
	public String SMTP_AUTH_USER = "smtpmailer@myprojectspace.co.in";
	public String SMTP_AUTH_PWD = "smtpmailer";
	public String emailMsgTxt = "Message Text";
	public String emailSubjectTxt = "Subject Text";
	public String emailFromAddress = "smtpmailer@myprojectspace.co.in";
	public static boolean response = false;

	public SendMailUsingAttachmentSMTP2() {
		;
	}

	public void postMail(String recipients[], String subject, String message,
			 String path, String fileName)// throws
														// MessagingException
	{
		try {
			boolean debug = false;

			// Set the host smtp address
			Properties props = new Properties();
			// props.put("mail.smtp.starttls.enable","true");
			props.put("mail.smtp.host", SMTP_HOST_NAME);
			// props.put("mail.smtp.auth", "true");
			// props.put("mail.smtp.port", "587");

			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);

			session.setDebug(debug);

			// create a message
			Message msg = new MimeMessage(session);

			// set the from and to address
			InternetAddress addressFrom = new InternetAddress(
					"smtpmailer@myprojectspace.co.in");
			msg.setFrom(addressFrom);

			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);
			// Setting the Subject and Content Type
			msg.setSubject(subject);

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(message);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			if (!path.equals("")) {
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(path);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(fileName);
				multipart.addBodyPart(messageBodyPart);
			}
			msg.setContent(multipart);

			// msg.setContent(message, "text/plain");
			Transport.send(msg);
			response = true;
		} catch (Exception e) {
			System.err.println("Error Mail : " + e);
			response = false;
			e.printStackTrace();
		}
	}

	/**
	 * SimpleAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
			String username = SMTP_AUTH_USER;
			String password = SMTP_AUTH_PWD;
			return new PasswordAuthentication(username, password);
		}
	}

}
