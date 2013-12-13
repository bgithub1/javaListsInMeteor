package me.kutrumbos;

import java.util.Calendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class GmailClient {

	private final String hostName = "smtp.gmail.com";
	private final String username;
	private final String password;
	private final Properties props;

	private Session session;
	
	public GmailClient(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	    this.session = null;
		
	    this.props = System.getProperties();
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", hostName);
	    props.put("mail.smtp.user", username);
	    props.put("mail.smtp.password", password);
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");
	    
	}
	
	public void connect(){
		this.session = Session.getDefaultInstance(props, null);
	}

	public void sendEmail(String[] recipList, String subject, String emailContents) 
			throws AddressException, MessagingException{
		if(session==null){
			System.err.println( "Null session, must connect first before sending emails");
		} else {
			MimeMessage message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(username));
		    InternetAddress[] toAddress = new InternetAddress[recipList.length];

		    // get array of recipient addresses
		    for( int i=0; i < recipList.length; i++ ) {
		        toAddress[i] = new InternetAddress(recipList[i]);
		    }

		    for( int i=0; i < toAddress.length; i++) {
		        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		    }
		    message.setSubject(subject);
		    message.setText(emailContents);
		    Transport transport = session.getTransport("smtp");
		    transport.connect(hostName, username, password);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		    System.out.println( "Email sent - "+Calendar.getInstance().getTime().toString());
		}
		
	}

	public void sendEmail(String[] toList, String[] bccList, String subject, String emailContents) 
			throws AddressException, MessagingException{
		if(session==null){
			System.err.println( "Null session, must connect first before sending emails");
		} else {
			MimeMessage message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(username));

		    for( int i=0; i < toList.length; i++ ) {
		    	message.addRecipient(Message.RecipientType.TO, new InternetAddress(toList[i]));
		    }

		    for( int i=0; i < bccList.length; i++ ) {
		    	message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccList[i]));
		    }

		    message.setSubject(subject);
		    message.setText(emailContents);
		    Transport transport = session.getTransport("smtp");
		    transport.connect(hostName, username, password);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		    System.out.println( "Email sent - "+Calendar.getInstance().getTime().toString());
		}
		
	}
	
	
}
