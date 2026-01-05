package com.xypha.onlineBus.booking.services;

import com.xypha.onlineBus.mail.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BookingEmailService {

    private final EmailService emailService;

    public BookingEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async("taskExecutor")
    public void sendBookingPendingEmail(
            String to,
            String bookingCode,
            Double totalAmount,
            List<String> seatNumbers,
            String source,
            String destination,
            OffsetDateTime departureDate
    ) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            String formattedDate = departureDate != null ? departureDate.format(formatter) : "-";
            String subject = "🚌 CozyBus Booking Pending";

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
                <h4>Please include booking code: <strong>%s</strong></h4>
                <p>Thank you for choosing CozyBus!</p>
                """.formatted(
                    bookingCode,
                    totalAmount,
                    String.join(", ", seatNumbers),
                    source,
                    destination,
                    formattedDate,
                    bookingCode
            );

            emailService.setEmail(to, subject, html, bookingCode);

        } catch (Exception e) {
            // log the error but do not fail the booking
            System.err.println("Failed to send pending booking email to " + to);
            e.printStackTrace();
        }
    }

    @Async("taskExecutor")
    public void sendConfirmedTicketEmail(
            String to,
            String bookingCode,
            String source,
            String destination,
            OffsetDateTime departureDate,
            List<String> seatNumbers,
            Double totalAmount
    ) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            String formattedDate = departureDate != null ? departureDate.format(formatter) : "-";
            String subject = "🚌 CozyBus Ticket Confirmed";

            String html = """
                <!DOCTYPE html>
                <html>
                <body style="background:#f2f2f2;padding:20px;font-family:Arial">
                  <div style="max-width:650px;margin:auto;background:white;border-radius:10px">
                    <div style="background:#1565c0;color:white;padding:20px;text-align:center">
                      <h2>🚌 CozyBus Travel Ticket</h2>
                      <p>Booking Confirmed</p>
                    </div>

                    <div style="padding:25px">
                      <p><strong>Booking Code:</strong> %s</p>
                      <p><strong>Route:</strong> %s → %s</p>
                      <p><strong>Departure:</strong> %s</p>
                      <p><strong>Seats:</strong> %s</p>
                      <p><strong>Total Paid:</strong> %,.0f MMK</p>

                      <hr/>
                      <p style="color:green;font-weight:bold">Status: CONFIRMED</p>
                    </div>

                    <div style="background:#f5f5f5;padding:15px;text-align:center">
                      Thank you for choosing <strong>CozyBus</strong>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                    bookingCode,
                    source,
                    destination,
                    formattedDate,
                    String.join(", ", seatNumbers),
                    totalAmount
            );

            emailService.setEmail(to, subject, html, bookingCode);

        } catch (Exception e) {
            System.err.println("Failed to send confirmed ticket email to " + to);
            e.printStackTrace();
        }
    }
}
