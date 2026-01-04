package com.xypha.onlineBus.booking.services;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component

public class BookingEmailListener {

    private final BookingEmailService emailService;
    private final BookingService bookingService;

    public BookingEmailListener(BookingEmailService emailService, BookingService bookingService) {
        this.emailService = emailService;
        this.bookingService = bookingService;
    }


    @TransactionalEventListener
    public void handleBookingCreated (BookingCreatedEvent event){
        emailService.sendBookingPendingEmail(
                event.email(),
                event.bookingCode(),
                event.totalAmount(),
                event.seatNumbers(),
                event.source(),
                event.destination(),
                event.departureDate()
        );
    }
}
