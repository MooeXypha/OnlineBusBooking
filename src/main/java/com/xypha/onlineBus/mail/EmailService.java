package com.xypha.onlineBus.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.Collections;
import java.util.Map;


@Service

public class EmailService {
    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    public void setEmail(String email, String subject, String htmlContent, String bookingCode){
        ApiClient defaultClient = new ApiClient();
        defaultClient.setApiKey(apiKey);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(defaultClient);

        SendSmtpEmail smtpEmail = new SendSmtpEmail()
                .sender(new SendSmtpEmailSender().email(senderEmail).name(senderName))
                .to(Collections.singletonList(new SendSmtpEmailTo().email(email)))
                .subject(subject)
                .htmlContent(htmlContent)
                .textContent("Booking is Pending. Your Code: "+bookingCode);


        try {
            apiInstance.sendTransacEmail(smtpEmail);
            System.out.println("Email sent successfully to: " + email);
        } catch (ApiException e) {
            System.err.println("Failed to send email to: " + email);
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response body: " + e.getResponseBody());
            e.printStackTrace();
        }


    }
}
