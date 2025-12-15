package com.xypha.onlineBus.buses.busTypeLayout.services;

import com.xypha.onlineBus.buses.busTypeLayout.entity.BusTypeLayout;
import com.xypha.onlineBus.buses.busTypeLayout.entity.Seat;
import com.xypha.onlineBus.buses.busTypeLayout.mapper.BusTypeLayoutMapper;
import com.xypha.onlineBus.buses.busTypeLayout.mapper.SeatMapper;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

private final BusMapper busMapper;
private final BusTypeLayoutMapper busTypeLayoutMapper;
private final SeatMapper seatMapper;


    public SeatService(BusMapper busMapper, BusTypeLayoutMapper busTypeLayoutMapper, SeatMapper seatMapper) {
        this.busMapper = busMapper;
        this.busTypeLayoutMapper = busTypeLayoutMapper;
        this.seatMapper = seatMapper;
    }

    /////////////////////Generate seats for a trip/////////
    public void generateSeatsForTrip (Long tripId, Long busTypeId, int totalSeats){
        BusTypeLayout layout = busTypeLayoutMapper.getBusTypeId(busTypeId);
        if (layout == null)
            throw new RuntimeException("Seat layout not found for bus types");

        int seatsPerRow = layout.getSeatPerRow();
        int rows = (int) Math.ceil((double) totalSeats /seatsPerRow);

        List<Seat> seats = new ArrayList<>();
        for ( int r = 0; r<rows; r++){
            char rowLetter = (char) ('A'+ r);
            for (int n=1; n<= seatsPerRow; n++){
                int seatNumberInt= r* seatsPerRow+n;
                if (seatNumberInt>totalSeats)break;

                Seat seat = new Seat();
                seat.setTripId(tripId);
                seat.setSeatNumber(rowLetter + String.valueOf(n));
                seat.setBooked(false);
                seat.add(seat);
            }
        }
        seats.forEach(seatMapper::createSeat);
    }

    public List<Seat> getSeatsByTrip (Long tripId){
        return seatMapper.getSeatByTrip(tripId);
    }

    public void bookSeat (Long tripId, String seatNumber){
        seatMapper.bookSeat(tripId, seatNumber);
    }

    public void avaliavleSeat (Long tripId, String seatNumber){
        seatMapper.avaliableSeat(tripId,seatNumber
        );
    }





}
