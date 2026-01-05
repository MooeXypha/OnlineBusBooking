package com.xypha.onlineBus.mail;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;


@Service
public class EmailService {

    private final SesClient sesClient;


    public EmailService(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    public void sendEmail (String toEmail, String subject, String htmlContent){

        SendEmailRequest request = SendEmailRequest.builder()
                .source("guguu957@gmail.com")
                .destination(Destination.builder()
                .toAddresses(toEmail)
                        .build())
                        .message(Message.builder()
                                .subject(Content.builder().data(subject).build())
                                .body(Body.builder()
                                        .html(Content.builder().data(htmlContent).build())
                                        .build())
                                .build())
                .build();
        sesClient.sendEmail(request);
        System.out.println("Email sent to "+ toEmail);

    }
}
