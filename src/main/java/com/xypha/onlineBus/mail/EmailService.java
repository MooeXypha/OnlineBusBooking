package com.xypha.onlineBus.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRestPasswordEmail(String toEmail,String username,String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Password Reset Request");

        String htmlMsg = "<h3>Password Reset Request</h3>"
                        +"<p> Hi,</p>"
                        +"<p>Click the link below to reset your password : </p>"
                        +"<a href='" +resetLink+ "' target= '_blank' >Rest Password </a>"
                        + "<p>If you did not request this, please ignore this email.</p>"
                        + "<p>Thanks!</p>";
        helper.setText(htmlMsg, true);
        mailSender.send(message);
    }
}
