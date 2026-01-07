package com.xypha.onlineBus.booking.services;

import com.xypha.onlineBus.mail.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SesException;
import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BookingEmailService {

    private final EmailService emailService;
    private final SesClient sesClient;

    public BookingEmailService(EmailService emailService, SesClient sesClient) {
        this.emailService = emailService;
        this.sesClient = sesClient;
    }

    @Async("taskExecutor")
    public void sendBookingPendingEmail(
            String to,
            String bookingCode,
            Double totalAmount,
            List<String> seatNumbers,
            String source,
            String destination,
            LocalDateTime departureDate
    ) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            String formattedDate = departureDate != null ? departureDate.format(formatter) : "-";
            String subject = "🚌 CozyBus Booking Pending";

            // HTML template with placeholders
            String html = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Booking Pending</title>
</head>
<body style="
margin:0;
padding:0;
background:linear-gradient(180deg,#f8fafc,#eef2ff);
font-family:'Segoe UI','Helvetica Neue',Inter,system-ui,Arial,sans-serif;
">
<table width="100%" cellpadding="0" cellspacing="0" style="padding:28px 12px;">
<tr>
<td align="center">
<table width="100%" cellpadding="0" cellspacing="0"
style="
max-width:680px;
background:linear-gradient(135deg,#facc15,#f97316,#f43f5e);
padding:2px;
border-radius:26px;
">
<tr>
<td>
<table width="100%" cellpadding="0" cellspacing="0"
style="
background:#ffffff;
border-radius:24px;
overflow:hidden;
box-shadow:0 24px 60px rgba(15,23,42,0.15);
">
<tr>
<td style="
padding:36px;
text-align:center;
background:radial-gradient(circle at top,#fef3c7,#ffffff);
">
<h1 style="margin:0;font-size:30px;color:#0f172a;letter-spacing:0.6px;">🚌 CozyBus Express</h1>
<p style="margin-top:10px;font-size:15px;color:#475569;">Booking Created – Payment Required</p>
</td>
</tr>

<tr>
<td align="center" style="padding:22px;">
<span style="
display:inline-block;
background:linear-gradient(90deg,#f59e0b,#d97706);
color:#ffffff;
padding:10px 28px;
border-radius:999px;
font-size:14px;
font-weight:600;
box-shadow:0 10px 24px rgba(245,158,11,0.4);
">⏳ BOOKING PENDING</span>
</td>
</tr>

<tr>
<td style="padding:0 36px 26px;color:#334155;">
<p style="margin:0;font-size:16px;line-height:1.8;">
Thank you for booking with <strong>CozyBus Express</strong>.<br>
Your booking is currently <strong>pending payment</strong>. Please complete payment to confirm your seat.
</p>
</td>
</tr>

<tr>
<td style="padding:0 36px 32px;">
<table width="100%" cellpadding="0" cellspacing="0"
style="background:linear-gradient(180deg,#ffffff,#f8fafc);border-radius:18px;border:1px solid #e5e7eb;">
<tr>
<td colspan="2" style="padding:16px;background:#f1f5f9;font-weight:600;color:#2563eb;border-radius:18px 18px 0 0;">
📄 Booking Summary
</td>
</tr>
<tr>
<td style="padding:14px;color:#64748b;">Booking Code</td>
<td style="padding:14px;font-weight:600;color:#f59e0b;">{{bookingCode}}</td>
</tr>
<tr style="background:#f8fafc;">
<td style="padding:14px;color:#64748b;">Route</td>
<td style="padding:14px;color:#0f172a;">{{source}} → {{destination}}</td>
</tr>
<tr>
<td style="padding:14px;color:#64748b;">Departure</td>
<td style="padding:14px;">{{departureDate}}</td>
</tr>
<tr style="background:#f8fafc;">
<td style="padding:14px;color:#64748b;">Seats</td>
<td style="padding:14px;">{{seatNumbers}}</td>
</tr>
<tr>
<td style="padding:14px;color:#64748b;">Total Amount</td>
<td style="padding:14px;font-weight:600;color:#f59e0b;">{{totalAmount}} MMK</td>
</tr>
</table>
</td>
</tr>
<!-- Payment Methods -->
<tr>
<td style="padding:0 36px 32px;">
<table width="100%" cellpadding="0" cellspacing="0"
style="background:#f1f5f9;border-radius:18px;border:1px solid #e5e7eb;">
<tr>
<td style="padding:16px;font-weight:600;color:#2563eb;border-radius:18px 18px 0 0;">
💰 Payment Methods
</td>
</tr>
<tr>
<td style="padding:16px;color:#334155;font-size:15px;line-height:1.6;">
<ul style="margin:0;padding-left:18px;">
<li><strong>KBZ Pay:</strong> {{kbzPayNumber}}</li>
<li><strong>WavePay:</strong> {{wavePayNumber}}</li>
<li><strong>Aya Pay:</strong> {{ayaPayNumber}}</li>
<li><strong>UAB Pay:</strong> {{uabPayNumber}}</li>
<li><strong>Yoma Pay:</strong> {{yomaPayNumber}}</li>
<li><strong>CB Pay:</strong> {{cbPayNumber}}</li>
</ul>
<p>Complete your payment within <strong>1 hours</strong> to secure your seat.</p>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td style="padding:26px;text-align:center;font-size:13px;color:#64748b;background:#f8fafc;">
Need help? <strong style="color:#2563eb;">support@cozybusexpress.com</strong><br>
© 2026 CozyBus Express. All rights reserved.
</td>
</tr>

</table>
</td>
</tr>
</table>
</td>
</tr>
</table>
</body>
</html>
""";

            // Replace placeholders
            html = html.replace("{{bookingCode}}", bookingCode)
                    .replace("{{source}}", source)
                    .replace("{{destination}}", destination)
                    .replace("{{departureDate}}", formattedDate)
                    .replace("{{seatNumbers}}", String.join(", ", seatNumbers))
                    .replace("{{totalAmount}}", String.format("%,.0f", totalAmount));

            // Send the email
            emailService.sendEmail(to, subject, html);

        } catch (Exception e) {
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
            LocalDateTime departureDate,
            List<String> seatNumbers,
            Double totalAmount
    ) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            String formattedDate = departureDate != null ? departureDate.format(formatter) : "-";
            String subject = "🚌 CozyBus Ticket Confirmed";

            // HTML template with placeholders
            String html = """
                                        <!DOCTYPE html>
                                               <html>
                                               <head>
                                               <meta charset="UTF-8">
                                               <title>Booking Confirmed</title>
                                               </head>
                                               <body style="
                                               margin:0;
                                               padding:0;
                                               background:linear-gradient(180deg,#f8fafc,#eef2ff);
                                               font-family:'Segoe UI','Helvetica Neue',Inter,system-ui,Arial,sans-serif;
                                               ">
                                               <table width="100%" cellpadding="0" cellspacing="0" style="padding:28px 12px;">
                                               <tr>
                                               <td align="center">
                                               
                                               <table width="100%" cellpadding="0" cellspacing="0"
                                               style="
                                               max-width:680px;
                                               background:linear-gradient(135deg,#60a5fa,#38bdf8,#22c55e);
                                               padding:2px;
                                               border-radius:26px;
                                               ">
                                               
                                               <tr>
                                               <td>
                                               
                                               <table width="100%" cellpadding="0" cellspacing="0"
                                               style="
                                               background:#ffffff;
                                               border-radius:24px;
                                               overflow:hidden;
                                               box-shadow:0 24px 60px rgba(15,23,42,0.15);
                                               ">
                                               
                                               <!-- Header -->
                                               <tr>
                                               <td style="
                                               padding:36px;
                                               text-align:center;
                                               background:radial-gradient(circle at top,#e0f2fe,#ffffff);
                                               ">
                                               <h1 style="margin:0;font-size:30px;color:#0f172a;letter-spacing:0.6px;">🚌 CozyBus Express</h1>
                                               <p style="margin-top:10px;font-size:15px;color:#475569;">Your Journey Is Confirmed</p>
                                               </td>
                                               </tr>
                                               
                                               <!-- Status -->
                                               <tr>
                                               <td align="center" style="padding:22px;">
                                               <span style="
                                               display:inline-block;
                                               background:linear-gradient(90deg,#22c55e,#16a34a);
                                               color:#ffffff;
                                               padding:10px 28px;
                                               border-radius:999px;
                                               font-size:14px;
                                               font-weight:600;
                                               box-shadow:0 10px 24px rgba(34,197,94,0.4);
                                               ">✔ BOOKING CONFIRMED</span>
                                               </td>
                                               </tr>
                                               
                                               <!-- Thank you message -->
                                               <tr>
                                               <td style="padding:0 36px 26px;color:#334155;">
                                               <p style="margin:0;font-size:16px;line-height:1.8;">
                                               Hello <strong>{{username}}</strong>,<br><br>
                                               Thank you for choosing <strong>CozyBus Express</strong>.<br>
                                               Your seat has been successfully reserved.
                                               </p>
                                               </td>
                                               </tr>
                                               
                                               <!-- Booking Details -->
                                               <tr>
                                               <td style="padding:0 36px 32px;">
                                               <table width="100%" cellpadding="0" cellspacing="0"
                                               style="background:linear-gradient(180deg,#ffffff,#f8fafc);border-radius:18px;border:1px solid #e5e7eb;">
                                               <tr>
                                               <td colspan="2" style="padding:16px;background:#f1f5f9;font-weight:600;color:#2563eb;border-radius:18px 18px 0 0;">
                                               📄 Booking Details
                                               </td>
                                               </tr>
                                               <tr>
                                               <td style="padding:14px;color:#64748b;">Booking Code</td>
                                               <td style="padding:14px;font-weight:600;color:#16a34a;">{{bookingCode}}</td>
                                               </tr>
                                               <tr style="background:#f8fafc;">
                                               <td style="padding:14px;color:#64748b;">Route</td>
                                               <td style="padding:14px;color:#0f172a;">{{source}} → {{destination}}</td>
                                               </tr>
                                               <tr>
                                               <td style="padding:14px;color:#64748b;">Departure</td>
                                               <td style="padding:14px;">{{departureDate}}</td>
                                               </tr>
                                               <tr style="background:#f8fafc;">
                                               <td style="padding:14px;color:#64748b;">Seats</td>
                                               <td style="padding:14px;">{{seatNumbers}}</td>
                                               </tr>
                                               <tr>
                                               <td style="padding:14px;color:#64748b;">Paid Amount</td>
                                               <td style="padding:14px;font-weight:600;color:#16a34a;">{{totalAmount}} MMK</td>
                                               </tr>
                                               </table>
                                               </td>
                                               </tr>
                                               
                                               <!-- Before You Board & Policies Section -->
                                               <tr>
                                               <td style="padding:0 36px 32px;">
                                               <table width="100%" cellpadding="0" cellspacing="0"
                                               style="background:#f1f5f9;border-radius:18px;border:1px solid #e5e7eb;">
                                               <tr>
                                               <td style="padding:16px;font-weight:600;color:#2563eb;border-radius:18px 18px 0 0;">
                                               📝 Before You Board & Policies
                                               </td>
                                               </tr>
                                               <tr>
                                               <td style="padding:16px;color:#334155;font-size:15px;line-height:1.6;">
                                               <ul style="margin:0;padding-left:18px;">
                                               <li>Please arrive at the boarding point at least <strong>15 minutes</strong> before departure.</li>
                                               <li>Bring your <strong>Booking Code</strong> and a valid <strong>ID/ NRC</strong>.</li>
                                               <li>Only <strong>registered passengers</strong> are allowed to board.</li>
                                               <li><strong>No refund</strong> is available for missed trips or cancellations after booking.</li>
                                               <li>Follow <strong>CozyBus Express policies</strong> regarding luggage, safety, and conduct.</li>
                                               <li>Keep your ticket and ID handy until the journey is completed.</li>
                                               <li>Children under 12 must be accompanied by an adult.</li>
                                               <li>Pets are not allowed unless explicitly stated.</li>
                                               <li>CozyBus Express is not responsible for lost personal belongings on the bus.</li>
                                               </ul>
                                               </td>
                                               </tr>
                                               </table>
                                               </td>
                                               </tr>
                                               
                                               <!-- Footer -->
                                               <tr>
                                               <td style="padding:26px;text-align:center;font-size:13px;color:#64748b;background:#f8fafc;">
                                               Need help? <strong style="color:#2563eb;">support@cozybusexpress.com</strong><br>
                                               © 2026 CozyBus Express. All rights reserved.
                                               </td>
                                               </tr>
                                               
                                               </table>
                                               </td>
                                               </tr>
                                               </table>
                                               
                                               </td>
                                               </tr>
                                               </table>
                                               </body>
                                               </html>
                                               
                    """;

            // ✅ Replace placeholders dynamically
            html = html.replace("{{bookingCode}}", bookingCode)
                    .replace("{{source}}", source)
                    .replace("{{destination}}", destination)
                    .replace("{{departureDate}}", formattedDate)
                    .replace("{{seatNumbers}}", String.join(", ", seatNumbers))
                    .replace("{{totalAmount}}", String.format("%,.0f", totalAmount));

            emailService.sendEmail(to, subject, html);

        } catch (Exception e) {
            System.err.println("Failed to send confirmed booking email to " + to);
            e.printStackTrace();
        }
    }


    @Async("taskExecutor")
    public void sendVerificationEmail(
            String to,
            String username
    ) {
            try {
                sesClient.verifyEmailIdentity(
                        VerifyEmailIdentityRequest.builder()
                                .emailAddress(to)
                                .build()
                );

                System.out.println("SES verification email sent to : "+ to);
            }catch (SesException sesException){
                System.out.println("SES verification failed for"+ to +": "+sesException.getMessage());
            }
            try{
                String subject = "🔐 Verify Your Email | CozyBus Express";

                String html = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Welcome to CozyBus Express</title>
</head>

<body style="
margin:0;
padding:0;
background:linear-gradient(180deg,#f8fafc,#eef2ff);
font-family:'Segoe UI','Helvetica Neue',Inter,system-ui,Arial,sans-serif;
">

<table width="100%" cellpadding="0" cellspacing="0" style="padding:28px 12px;">
<tr>
<td align="center">

<table width="100%" cellpadding="0" cellspacing="0"
style="
max-width:680px;
background:linear-gradient(135deg,#60a5fa,#38bdf8,#22c55e);
padding:2px;
border-radius:26px;
">

<tr>
<td>

<table width="100%" cellpadding="0" cellspacing="0"
style="
background:#ffffff;
border-radius:24px;
overflow:hidden;
box-shadow:0 24px 60px rgba(15,23,42,0.15);
">

<!-- Header -->
<tr>
<td style="
padding:40px;
text-align:center;
background:radial-gradient(circle at top,#e0f2fe,#ffffff);
">
<h1 style="margin:0;font-size:32px;color:#0f172a;">
🎉 Welcome to CozyBus Express
</h1>
<p style="margin-top:12px;font-size:16px;color:#475569;">
Your account is ready — let’s get moving
</p>
</td>
</tr>

<!-- Glow Welcome Badge -->
<tr>
<td align="center" style="padding:22px;">
<span style="
display:inline-block;
background:linear-gradient(90deg,#22c55e,#16a34a);
color:#ffffff;
padding:12px 32px;
border-radius:999px;
font-size:14px;
font-weight:600;
box-shadow:0 12px 28px rgba(34,197,94,0.45);
">
🚀 ACCOUNT SUCCESSFULLY CREATED
</span>
</td>
</tr>

<!-- Greeting -->
<tr>
<td style="padding:0 36px 26px;color:#334155;">
<p style="margin:0;font-size:16px;line-height:1.9;">
Hello <strong>{{username}}</strong>,<br><br>

Welcome aboard! Your <strong>CozyBus Express</strong> account has been successfully created using this email address.
You’re now part of a smoother, smarter, and more comfortable way to travel.
</p>
</td>
</tr>

<!-- Feature Cards -->
<tr>
<td style="padding:0 36px 32px;">
<table width="100%" cellpadding="0" cellspacing="0"
style="border-radius:18px;border:1px solid #e5e7eb;background:#f8fafc;">

<tr>
<td style="padding:18px;font-size:15px;">
🚌 <strong>Easy Ticket Booking</strong><br>
<span style="color:#64748b;">Reserve seats in seconds</span>
</td>
</tr>

<tr style="background:#ffffff;">
<td style="padding:18px;font-size:15px;">
📍 <strong>Live Routes & Schedules</strong><br>
<span style="color:#64748b;">Plan your journey confidently</span>
</td>
</tr>

<tr>
<td style="padding:18px;font-size:15px;">
💺 <strong>Seat Selection</strong><br>
<span style="color:#64748b;">Choose comfort your way</span>
</td>
</tr>

</table>
</td>
</tr>

<!-- Closing Message -->
<tr>
<td style="padding:0 36px 32px;">
<p style="margin:0;font-size:15px;color:#334155;line-height:1.8;">
We’re excited to have you with us.<br>
Sit back, relax, and <strong>enjoy every journey with CozyBus Express</strong> 🚌✨
</p>
</td>
</tr>

<!-- Footer -->
<tr>
<td style="
padding:26px;
text-align:center;
font-size:13px;
color:#64748b;
background:#f8fafc;
">
Need help? <strong style="color:#2563eb;">support@cozybusexpress.com</strong><br>
© 2026 CozyBus Express. All rights reserved.
</td>
</tr>

</table>
</td>
</tr>
</table>

</td>
</tr>
</table>

</body>
</html>

""";

                // ✅ Dynamic replacement (SAFE)
                html = html.replace("{{username}}", username != null ? username : "User");

                emailService.sendEmail(to, subject, html);


            } catch (Exception e) {
            System.err.println("Failed to send verification email to " + to + " : " + e.getMessage());
             }
    }



}
