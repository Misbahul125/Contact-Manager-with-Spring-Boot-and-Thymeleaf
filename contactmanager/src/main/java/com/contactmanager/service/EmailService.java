package com.contactmanager.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.contactmanager.models.EmailData;

@Service
public class EmailService {

	public boolean sendEmail(EmailData emailData) {
        boolean test = false;

        final String toEmail = emailData.getEmail();
        final String fromEmail = "misbahulhaque2001@gmail.com";
        final String password = "plxmjcyteimnfptq";

        try {

            // your host email smtp server details
            Properties pr = new Properties();
            pr.setProperty("mail.smtp.host", "smtp.gmail.com");
            pr.setProperty("mail.smtp.port", "587");
            pr.setProperty("mail.smtp.auth", "true");
            pr.setProperty("mail.smtp.starttls.enable", "true");
            pr.put("mail.smtp.socketFactory.port", "587");
            pr.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            //get session to authenticate the host email address and password
            Session session = Session.getInstance(pr, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });
            
            session.setDebug(true);

            //set email message details
            Message message = new MimeMessage(session);

            //set from email address
            message.setFrom(new InternetAddress(fromEmail));

            //set to email address or destination email address
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            
            StringBuffer sb = new StringBuffer();

            sb.append("Your OTP (One Time Password) to reset password at Contact Manager is :-  <strong>").append(emailData.getCode()).append("</strong>.");
            
            sb.append("<br/>");
            sb.append("It is a system generated email. Please do not reply.");
            
            sb.append("<br/>");
            sb.append("<br/>");
            sb.append("Thank you.");
            
            sb.append("<br/>");
            sb.append("<br/>");
            sb.append("Regards,");
            sb.append("<br/>");
            sb.append("Team Contact Manager");
            
            //set email subject
            message.setSubject("Contact Manager - Reset Password");

            //set message text
            message.setContent(sb.toString(), "text/html;charset=UTF-8");

            //send the message
            Transport.send(message);

            test = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return test;
    }
	
}
