package com.xypha.onlineBus.booking.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingEmailService {

    private final JavaMailSender mailSender;


    public BookingEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingPendingEmail (
            String to,
            String bookingCode,
            Double totalAmount,
            List<String> seatNumbers,
            String source,
            String destination,
            LocalDateTime departureDate
    ){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("🚌 CozyBus Booking Pending");
            helper.setFrom("CozyBus <chanarr633@gmail.com>");

            String html = """
    <h2>Booking Pending</h2>
    <p>Thank you for booking with CozyBus! Your booking is currently pending payment.</p>
    <p><strong>Booking Code:</strong> %s</p>
    <p><strong>Total Amount:</strong> %,.0f MMK</p>
    <p><strong>Seats:</strong> %s</p>
    <p><strong>From:</strong> %s</p>
    <p><strong>To:</strong> %s</p>
    <p><strong>Departure Date:</strong> %s</p>
    <hr>
    <p>Please transfer payment to:</p>
    <ul>
      <li>KBZ: 09254042303</li>
      <li>AYA: 095049213</li>
      <li>CB: 09301234567</li>
      <li>Wave Pay: 0976543210</li>
    </ul>
    <p>We will confirm your booking after payment.</p>
    <h4>In payment form, please include your booking code: <strong>%s</strong></h4>
    <p>If you have any questions, feel free to contact our support team.</p>
    <p>Thank you for choosing CozyBus!</p>
    """.formatted(
                    bookingCode,                    // %s Booking Code
                    totalAmount,                     // %,.0f Total Amount
                    String.join(", ", seatNumbers),  // %s Seats
                    source,                          // %s From
                    destination,                     // %s To
                    departureDate,                   // %s Departure Date
                    bookingCode                      // %s Booking code in payment form
            );

            helper.setText(html,true);
            mailSender.send(message);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
