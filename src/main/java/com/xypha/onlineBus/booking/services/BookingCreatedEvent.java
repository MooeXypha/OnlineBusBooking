package com.xypha.onlineBus.booking.services;

import java.time.LocalDateTime;
import java.util.List;

public record BookingCreatedEvent(
        String email,
        String bookingCode,
        Double totalAmount,
        List<String> seatNumbers,
        String source,
        String destination,
        LocalDateTime departureDate
) {

}
