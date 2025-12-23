package com.xypha.onlineBus.booking.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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


    public void sendConfirmedTicketEmail(
            String to,
            String bookingCode,
            String source,
            String destination,
            LocalDateTime departureDate,
            List<String> seatNumbers,
            Double totalAmount
    )
    {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom("CozyBus <no-reply@cozybus.com>");

            String htmlDesign = """
                    <!DOCTYPE html>
                                <html>
                                <body style="background:#f2f2f2;padding:20px;font-family:Arial">
                                  <div style="max-width:650px;margin:auto;background:white;
                                              border-radius:10px;overflow:hidden">
                                    <div style="background:#1565c0;color:white;padding:20px;text-align:center">
                                      <h2>🚌 CozyBus Travel Ticket</h2>
                                      <p>Booking Confirmed</p>
                                    </div>
                                          
                                    <div style="padding:25px">
                                      <p><strong>Booking Code:</strong> %s</p>
                                      <p><strong>Route:</strong> %s → %s</p>
                                      <p><strong>Departure:</strong> %s</p>
                                      <p><strong>Seats:</strong> %s</p>
                                      <p><strong>Total Paid:</strong> %s MMK</p>
                                          
                                      <hr/>
                                          
                                      <p style="color:green;font-weight:bold">
                                        Status: CONFIRMED
                                      </p>
                                          
                                      <p>Please show this ticket at boarding time.</p>
                                    </div>
                                    
                                    <div>
                                    <h3>🚌 Terms & Conditions</h3>
                                    </div>
                                    <div>
                                    <ul>
                                      <li>🕒 Arrive at least 30 minutes before departure with your ticket.</li>
                                      <li>🪪 Bring a valid ID (NRC, passport, or official document) that matches the ticket name.</li>
                                      <li>🧳 Carry only allowed luggage and keep your belongings secure.</li>
                                      <li>❌ Tickets cannot be transferred or handed over to another person.</li>
                                      <li>💳 Tickets are non-refundable for late arrivals, no-shows, or cancellation after the allowed time.</li>
                                      <li>🚫 Smoking, alcohol, loud behavior, or damaging bus property is strictly prohibited.</li>
                                    </ul>
                                    </div>
                                          
                                    <div style="background:#f5f5f5;padding:15px;text-align:center">
                                      Thank you for choosing <strong>CozyBus</strong>
                                    </div>
                                  </div>
                                </body>
                                </html> 
                       """.formatted(
                               bookingCode,                     // %s Booking Code
                               source,
                                destination,
                                departureDate,
                                String.join(", ", seatNumbers),
                                String.format("%,.0f", totalAmount)
            );

            helper.setText(htmlDesign, true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
