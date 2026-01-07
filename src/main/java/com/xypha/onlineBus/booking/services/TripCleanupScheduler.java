package com.xypha.onlineBus.booking.services;

import com.xypha.onlineBus.trip.mapper.TripMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class TripCleanupScheduler {

    private final TripMapper tripMapper;
    private final BookingService bookingService;

    public TripCleanupScheduler(TripMapper tripMapper, BookingService bookingService) {
        this.tripMapper = tripMapper;
        this.bookingService = bookingService;
    }

    private static final ZoneId MYANMAR_ZONE = ZoneId.of("Asia/Yangon");

    @Scheduled (cron = "0 0 1 * * ?")
    public void cleanupPastTrips(){
        LocalDateTime now = LocalDateTime.now(MYANMAR_ZONE);

        List<Long> pastTripIds = tripMapper.getTripsWithArrivalBefore(now.minusDays(1));
        for (Long tripId : pastTripIds){
            try{
                bookingService.cancelTripAndReleaseSeats(tripId);
                tripMapper.deleteTrip(tripId);
                System.out.println("Delete past trip :"+ tripId);
            }catch (Exception e){
                System.out.println("Failed to delete trip "+tripId+ ": "+e.getMessage());
            }
        }
    }

}
