package com.xypha.onlineBus.booking.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingCleanupScheduler {
    private final BookingCleanupService bookingCleanupService;
    private final TripCleanupScheduler tripCleanupScheduler;


    public BookingCleanupScheduler(BookingCleanupService bookingCleanupService, TripCleanupScheduler tripCleanupScheduler) {
        this.bookingCleanupService = bookingCleanupService;
        this.tripCleanupScheduler = tripCleanupScheduler;
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) //every 10 minutes
    public void runCleanup(){
        bookingCleanupService.deletePastTrips();
        bookingCleanupService.autoCancelExpiredPendingBookings();
        bookingCleanupService.deleteOldCancelledBookings();
    }

}
