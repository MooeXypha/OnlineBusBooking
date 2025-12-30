package com.xypha.onlineBus.booking.services;

import com.xypha.onlineBus.booking.entity.Booking;
import com.xypha.onlineBus.booking.mapper.BookingMapper;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingCleanupService {
    private final BookingMapper bookingMapper;
    private final SeatMapper seatMapper;

    public BookingCleanupService(BookingMapper bookingMapper, SeatMapper seatMapper) {
        this.bookingMapper = bookingMapper;
        this.seatMapper = seatMapper;
    }


    @Transactional
    public void autoCancelExpiredPendingBookings (){
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expired = bookingMapper.findExpiredPendingBookings(now);

        for (Booking booking : expired){
            bookingMapper.updateStatus(booking.getBookingCode(), "CANCELLED");

            List<Long> seatIds = bookingMapper.getSeatIdsByBookingId(booking.getId());

            for (Long seatId: seatIds){
                seatMapper.updateSeatStatus(seatId, 0);
            }
        }
    }

    @Transactional
    public void deleteOldCancelledBookings(){
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        bookingMapper.deleteOldCancelledBookings(cutoff);
    }



}
