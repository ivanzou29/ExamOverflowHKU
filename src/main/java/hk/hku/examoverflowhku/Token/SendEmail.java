package hk.hku.examoverflowhku.Token;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {


    public static void sendMail(String destination, String code) {
        String to = destination;

        String from = "examoverflow@126.com";
        final String username = "examoverflow@126.com";
        final String password = "comp3330";

        String host = "smtp.126.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "465");// use one of the options in the SMTP settings tab in your Mailtrap Inbox

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            message.setSubject("ExamOverflow@HKU: Your Registration Token Code");

            message.setText("Hi HKUer!\n\n" +
                            "Welcome to ExamOverflow@HKU! Your registration token code is: " + code + ".\n\n" +
                            "ExamOverflow@HKU");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}