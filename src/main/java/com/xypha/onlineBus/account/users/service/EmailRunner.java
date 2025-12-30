package com.xypha.onlineBus.account.users.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailRunner implements CommandLineRunner {

private final JavaMailSender mailSender;


    public EmailRunner(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void run(String... args) throws Exception {
        try{
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo("bhonemyat9167@gmail.com");
            msg.setSubject("Render SMTP Test");
            msg.setText("If you receive this, SMTP works");
            mailSender.send(msg);
            System.out.println("Email sent successfully");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
