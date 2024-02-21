package com.ayalait.gesventas.utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSenderService {
	private static final Properties properties = new Properties();
	
	private static String password;

	private static Session session;

	private static void init() {

		properties.put("mail.smtp.host", "mail.ayalait.com");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port",25);
		properties.put("mail.smtp.mail.sender","info@ayalait.com");
		properties.put("mail.smtp.user", "info@ayalait.com");
		properties.put("mail.smtp.auth", "true");

		session = Session.getDefaultInstance(properties);
	}
	
	public static void main(String[] args) {
		sendEmail();

	}
	
	public static void sendEmail(){

		init();
		try{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress((String)properties.get("mail.smtp.mail.sender")));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("info@ayalait.com"));
			message.setSubject("Prueba");
			message.setText("Texto");
			Transport t = session.getTransport("smtp");
			t.connect((String)properties.get("mail.smtp.user"), "Informacion123");
			t.sendMessage(message, message.getAllRecipients());
			t.close();
		}catch (MessagingException me){
                        //Aqui se deberia o mostrar un mensaje de error o en lugar
                        //de no hacer nada con la excepcion, lanzarla para que el modulo
                        //superior la capture y avise al usuario con un popup, por ejemplo.
			return;
		}
		
	}

}