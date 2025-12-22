package com.xypha.onlineBus.buses.seat.services;

import com.xypha.onlineBus.buses.seat.entity.Seat;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatSelectionService {

    private final SeatMapper seatMapper;


    public SeatSelectionService(SeatMapper seatMapper) {
        this.seatMapper = seatMapper;
    }


    public void validateSelectSeats (Long tripId, List<String> selectedSeats){
        for(String seatNo : selectedSeats){
            Seat seat = seatMapper.getSeatByTripAndNo(tripId, seatNo);
            if (seat == null) {
                throw new RuntimeException("Seat"  + seatNo + " does not exist for trip " + tripId );

            }if (seat.getStatus() == 1){
                throw new RuntimeException("Seat " + seatNo + " is already booked.");
            }
        }
    }
}
