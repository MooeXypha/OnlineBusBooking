package com.xypha.onlineBus.booking.services;

import com.xypha.onlineBus.booking.entity.Booking;
import com.xypha.onlineBus.booking.mapper.BookingMapper;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import com.xypha.onlineBus.trip.mapper.TripMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingCleanupService {
    private final BookingMapper bookingMapper;
    private final SeatMapper seatMapper;
    private final TripMapper tripMapper;

    public BookingCleanupService(BookingMapper bookingMapper, SeatMapper seatMapper, TripMapper tripMapper) {
        this.bookingMapper = bookingMapper;
        this.seatMapper = seatMapper;
        this.tripMapper = tripMapper;
    }


    @Transactional
    public void autoCancelExpiredPendingBookings (){

        LocalDateTime now = LocalDateTime.now();
        List<Long> bookingIds  = bookingMapper.findExpiredPendingBookings(now);
        if (bookingIds.isEmpty())return;

        bookingMapper.cancelBookingsByIds(bookingIds, now);

        seatMapper.releaseSeatsByBookingIds(bookingIds);
    }

    @Transactional
    public void deleteOldCancelledBookings(){
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        bookingMapper.deleteOldCancelledBookings(cutoff);
    }



}
