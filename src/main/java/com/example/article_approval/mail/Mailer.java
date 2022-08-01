package com.example.article_approval.mail;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mailer {
    public static void sendEmail(String destEmailId, String content, String subject) {
        final String senderEmailId = "daman.arora@sprinklr.com";
        final String uname = "daman.arora@sprinklr.com";
        final String pwd = "nmrfknbasnunjbpg";

        //Set properties and their values
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");


        //Create a Session object & authenticate uid and pwd
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(uname, pwd);
                    }
                });

        try {
            //Create MimeMessage object & set values
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmailId));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destEmailId));
            message.setSubject(subject);
            message.setText(content);
            //Now send the message
            Transport.send(message);
        } catch (MessagingException exp) {
            throw new RuntimeException(exp);
        }
    }
}
