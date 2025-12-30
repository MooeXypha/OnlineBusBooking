//package com.xypha.onlineBus.booking.services;
//
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class BookingEmailService {
//
// private final EmailService emailService;
//
//    public BookingEmailService(EmailService emailService) {
//        this.emailService = emailService;
//    }
//
//    public void sendBookingPendingEmail(
//            String to,
//            String bookingCode,
//            Double totalAmount,
//            List<String> seatNumbers,
//            String source,
//            String destination,
//            LocalDateTime departureDate
//    ) {
//
//        String subject = "🚌 CozyBus Booking Pending";
//
//        String html = """
//            <h2>Booking Pending</h2>
//            <p>Thank you for booking with CozyBus! Your booking is currently pending payment.</p>
//            <p><strong>Booking Code:</strong> %s</p>
//            <p><strong>Total Amount:</strong> %,.0f MMK</p>
//            <p><strong>Seats:</strong> %s</p>
//            <p><strong>From:</strong> %s</p>
//            <p><strong>To:</strong> %s</p>
//            <p><strong>Departure Date:</strong> %s</p>
//            <hr>
//            <p>Please transfer payment to:</p>
//            <ul>
//              <li>KBZ: 09254042303</li>
//              <li>AYA: 095049213</li>
//              <li>CB: 09301234567</li>
//              <li>Wave Pay: 0976543210</li>
//            </ul>
//            <h4>Please include booking code: <strong>%s</strong></h4>
//            <p>Thank you for choosing CozyBus!</p>
//            """.formatted(
//                bookingCode,
//                totalAmount,
//                String.join(", ", seatNumbers),
//                source,
//                destination,
//                departureDate,
//                bookingCode
//        );
//
//        emailService.sendHtmlEmail(to, subject, html);
//    }
//
//    public void sendConfirmedTicketEmail(
//            String to,
//            String bookingCode,
//            String source,
//            String destination,
//            LocalDateTime departureDate,
//            List<String> seatNumbers,
//            Double totalAmount
//    ) {
//
//        String subject = "🚌 CozyBus Ticket Confirmed";
//
//        String html = """
//            <!DOCTYPE html>
//            <html>
//            <body style="background:#f2f2f2;padding:20px;font-family:Arial">
//              <div style="max-width:650px;margin:auto;background:white;border-radius:10px">
//                <div style="background:#1565c0;color:white;padding:20px;text-align:center">
//                  <h2>🚌 CozyBus Travel Ticket</h2>
//                  <p>Booking Confirmed</p>
//                </div>
//
//                <div style="padding:25px">
//                  <p><strong>Booking Code:</strong> %s</p>
//                  <p><strong>Route:</strong> %s → %s</p>
//                  <p><strong>Departure:</strong> %s</p>
//                  <p><strong>Seats:</strong> %s</p>
//                  <p><strong>Total Paid:</strong> %,.0f MMK</p>
//
//                  <hr/>
//                  <p style="color:green;font-weight:bold">Status: CONFIRMED</p>
//                </div>
//
//                <div style="background:#f5f5f5;padding:15px;text-align:center">
//                  Thank you for choosing <strong>CozyBus</strong>
//                </div>
//              </div>
//            </body>
//            </html>
//            """.formatted(
//                bookingCode,
//                source,
//                destination,
//                departureDate,
//                String.join(", ", seatNumbers),
//                totalAmount
//        );
//
//        emailService.sendHtmlEmail(to, subject, html);
//    }
//}
