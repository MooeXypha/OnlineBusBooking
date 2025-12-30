package com.xypha.onlineBus.booking.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingCleanupScheduler {
    private final BookingCleanupService bookingCleanupService;

    public BookingCleanupScheduler(BookingCleanupService bookingCleanupService) {
        this.bookingCleanupService = bookingCleanupService;
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) //every 10 minutes
    public void runCleanup(){
        bookingCleanupService.autoCancelExpiredPendingBookings();
        bookingCleanupService.deleteOldCancelledBookings();
    }

}
