package org.openxava.util;

import java.io.*;
import java.util.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.commons.logging.*;

/**
 * @author Janesh Kodikara
 * @author Denis Torres 
 */

public class Emails {
	
	private static Log log = LogFactory.getLog(Emails.class);  
		
	private static class BackgroundSender implements Runnable {
		
		private String fromEmail;
		private String toEmail;
		private String subject;
		private String content;

		public BackgroundSender(String fromEmail, String toEmail, String subject, String content) {
			this.fromEmail = fromEmail;
			this.toEmail = toEmail;
			this.subject = subject;
			this.content = content;
		}

		public void run() {
			try {
				Emails.send(fromEmail, toEmail, subject, content); 
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("email_sending_error"), ex);
			}
		}
		
	}

	public static class Attachment {
		private String name;
		private File file;
		
		public Attachment(String name, File file){
			this.name = name;
			this.file = file;
		}
	}

	private static class SMTPAuthenticator extends javax.mail.Authenticator {
	    private String fUser;
	    private String fPassword;

	    public SMTPAuthenticator(String user, String password) {
	        fUser = user;
	        fPassword = password;
	    }

	    public PasswordAuthentication getPasswordAuthentication() {
	        return new PasswordAuthentication(fUser, fPassword);
	    }
	}
	
	private final static String MESSAGE_CONTENT_TYPE = "text/html; charset=" + XSystem.getEncoding(); 


    public Emails() {

    }

    public static void send(String smtpHost, int smtpPort,
                            String fromEmail, String toEmail,
                            String subject, String content)
            throws AddressException, MessagingException {        

        // Create a mail session
        Session session = getMailSession(smtpHost, smtpPort);

        // Construct the message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail));
        msg = setTORecipients(msg, toEmail);
        msg.setSubject(subject);
        msg.setContent(content, MESSAGE_CONTENT_TYPE);

        // Send the message
        Transport.send(msg);

    }


    public static void send(String smtpHost, int smtpPort,
                            String fromEmail, String toEmail, String ccEmail,
                            String subject, String content)
            throws AddressException, MessagingException {

        // Create a mail session
        Session session = getMailSession(smtpHost, smtpPort);

        // Construct the message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail));
        msg = setTORecipients(msg, toEmail);
        msg = setCCRecipients(msg, ccEmail);
        msg.setSubject(subject);
        msg.setContent(content, MESSAGE_CONTENT_TYPE);

        // Send the message
        Transport.send(msg);

    }
    
    /**
     * @since 5.9
     */
    public static void sendInBackground(String toEmail, String subject, String content) {  
    	new Thread(new BackgroundSender(XavaPreferences.getInstance().getSMTPFromEmail(), toEmail, subject, content)).start(); 
    }
    
    /**
     * @since 5.9
     */    
    public static void sendInBackground(String fromEmail, String toEmail, 
		String subject, String content) 
    {
    	new Thread(new BackgroundSender(fromEmail, toEmail, subject, content)).start();
    }

    public static void send(String fromEmail, String toEmail,
			String subject, String content, Attachment... attachments)
		throws AddressException, MessagingException {
		
		// Create a mail session
		Session session = getMailSession();
		
		// Construct the message
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(fromEmail));
		msg = setTORecipients(msg, toEmail);
		msg.setSubject(subject);
		// content and attachments
		if (attachments != null && attachments.length > 0 ) addContentAndAttachments(msg, content, attachments);
		else msg.setContent(content, MESSAGE_CONTENT_TYPE);
		
		// Send the message
		Transport.send(msg);
	}

    public static void send(String fromEmail, String senderName, String toEmail,
                            String subject, String content)
            throws AddressException, MessagingException, UnsupportedEncodingException{

        // Create a mail session
        Session session = getMailSession();

        // Construct the message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail, senderName));
        msg = setTORecipients(msg, toEmail);
        msg.setSubject(subject);
        msg.setContent(content, MESSAGE_CONTENT_TYPE);

        // Send the message
        Transport.send(msg);
    }


    public static void send(String fromEmail, String senderName, String toEmail, String ccEmail,
                            String subject, String content)
            throws AddressException, MessagingException, UnsupportedEncodingException {

        // Create a mail session
        Session session = getMailSession();

        // Construct the message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail, senderName));
        msg = setTORecipients(msg, toEmail);
        msg = setCCRecipients(msg, ccEmail);
        msg.setSubject(subject);
        msg.setContent(content, MESSAGE_CONTENT_TYPE);

        // Send the message
        Transport.send(msg);
    }

    private static Session getMailSession() {
    	Session session = null;
    	
        String mailUser;
        String mailUserPassword;
        String smtpHost;
        int smtpPort;

        smtpHost = XavaPreferences.getInstance().getSMTPHost();
        smtpPort = XavaPreferences.getInstance().getSMTPPort();

        mailUser = XavaPreferences.getInstance().getSMTPUserID();
        mailUserPassword = XavaPreferences.getInstance().getSMTPUserPassword();
        
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", "" + smtpPort);
        
        if(XavaPreferences.getInstance().isSMTPStartTLSEnable()){
        	props.put("mail.smtp.starttls.enable", "true");
        }
        
        if (XavaPreferences.getInstance().isSMTPHostTrusted()) {
        	props.put("mail.smtp.ssl.trust", smtpHost);
		}
        
        if(!Is.empty(mailUser) || !Is.empty(mailUserPassword)){
        	props.put("mail.smtp.user", mailUser);
        	props.put("mail.smtp.auth", "true");
        	Authenticator auth = new SMTPAuthenticator(mailUser, mailUserPassword);
        	session = Session.getDefaultInstance(props, auth);
        } else {
        	session = Session.getDefaultInstance(props);
        }

        return session;
    }


    private static Session getMailSession(String smtpHost,int smtpPort ) {
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", "" + smtpPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "false");

        Session session = Session.getDefaultInstance(props);
        return session;
    }

    private static Message setTORecipients(Message msg, String emails) throws MessagingException {

        int countEmails;
        StringTokenizer emailList = new StringTokenizer(emails, ",");
        countEmails = emailList.countTokens();

        InternetAddress[] address = new InternetAddress[countEmails];
        for (int i = 0; i < countEmails; i ++) {
            address[i] = new InternetAddress(emailList.nextToken());
        }
        msg.setRecipients(Message.RecipientType.TO, address);
        return msg;
    }

    private static Message setCCRecipients(Message msg, String emails) throws MessagingException {
        int countEmails;
        StringTokenizer emailList = new StringTokenizer(emails, ",");
        countEmails = emailList.countTokens();

        InternetAddress[] address = new InternetAddress[countEmails];
        for (int i = 0; i < countEmails; i ++) {
            address[i] = new InternetAddress(emailList.nextToken());
        }
        msg.setRecipients(Message.RecipientType.CC, address);
        return msg;
    }
    
    /** @since v4m6 */
    private static void addContentAndAttachments(Message msg, String content, Attachment... attachments) 
			throws MessagingException{
		
		Multipart multipart = new MimeMultipart();
		// content
		MimeBodyPart messagePart = new MimeBodyPart();
		messagePart.setText(content);
		multipart.addBodyPart(messagePart);
		// attachments
		for (int i = 0; i < attachments.length; i++){
			MimeBodyPart attachmentPart = new MimeBodyPart();
			attachmentPart.setDataHandler(new DataHandler(new FileDataSource(attachments[i].file)));
			attachmentPart.setFileName(attachments[i].name);
			
			multipart.addBodyPart(attachmentPart);	
		}
		//
		msg.setContent(multipart);
    }

}