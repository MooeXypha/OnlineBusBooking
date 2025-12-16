package com.xypha.onlineBus.buses.seat.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.buses.seat.entity.Seat;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

private final BusMapper busMapper;
private final SeatMapper seatMapper;


    public SeatService(BusMapper busMapper, SeatMapper seatMapper) {
        this.busMapper = busMapper;
        this.seatMapper = seatMapper;
    }

    /////////////////////Generate seats for a trip/////////
   public void generateSeatsForTrip (Long tripId, Long busId){
        if (seatMapper.countByTripId(tripId) > 0){
            return; // Seats already generated for this trip
        }
        Bus bus = busMapper.getBusById(busId);
        int totalSeats = bus.getTotalSeats();
        int seatsPerRow = bus.getBusType().getSeatPerRow();
        int rows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        List<Seat> seats = new ArrayList<>();

        for (int r = 0; r<rows; r++){
            char row = (char) ('A' + r);
            for (int c =1; c<=seatsPerRow; c++){
                int seatNumber = r * seatsPerRow + c;
                if (seatNumber > totalSeats){
                    break; // No more seats to generate
                }
                Seat seat = new Seat();
                seat.setTripId(tripId);
                seat.setSeatNo(row + String.valueOf(c));
                seat.setStatus(0); // 0 means available
                seats.add(seat);
            }
        }
        seats.forEach(seatMapper::createSeat);
   }


    // ================= CREATE SEAT =================
    public ApiResponse<Seat> createSeat(Seat seat) {

        seatMapper.createSeat(seat);
        return new ApiResponse<>("SUCCESS", "Seat created successfully.", seat);
    }

    // ================= UPDATE SEAT =================
    public ApiResponse<Seat> updateSeat(Long seatId, Seat seatUpdate) {
        Seat seat = seatMapper.getSeatById(seatId);
        if (seat == null) {
            return new ApiResponse<>("NOT_FOUND", "Seat not found.", null);
        }

        seat.setSeatNo(seatUpdate.getSeatNo());
        seat.setStatus(seatUpdate.getStatus());
        seatMapper.updateSeat(seat);

        return new ApiResponse<>("SUCCESS", "Seat updated successfully.", seat);
    }

    // ================= GET SEAT BY ID =================
    public ApiResponse<Seat> getSeatById(Long seatId) {
        Seat seat = seatMapper.getSeatById(seatId);
        if (seat == null) {
            return new ApiResponse<>("NOT_FOUND", "Seat not found.", null);
        }
        return new ApiResponse<>("SUCCESS", "Seat retrieved successfully.", seat);
    }

    // ================= GET ALL SEATS =================
//    public ApiResponse<List<Seat>> getAllSeats() {
//        List<Seat> seats = seatMapper.ge();
//        return new ApiResponse<>("SUCCESS", "Seats retrieved successfully.", seats);
//    }

    // ================= GET ALL SEATS PAGINATED =================
    public ApiResponse<PaginatedResponse<Seat>> getSeatsPaginated(int offset, int limit) {
        if (offset < 0) offset = 0;
        if (limit < 1) limit = 10;

        List<Seat> seats = seatMapper.getAllPaginated(offset, limit);
        int total = seatMapper.countSeats();

        PaginatedResponse<Seat> paginated = new PaginatedResponse<>(offset, limit, total, seats);
        return new ApiResponse<>("SUCCESS", "Seats retrieved successfully.", paginated);
    }

    // ================= DELETE SEAT =================
    public ApiResponse<Void> deleteSeat(Long seatId) {
        Seat seat = seatMapper.getSeatById(seatId);
        if (seat == null) {
            return new ApiResponse<>("NOT_FOUND", "Seat not found.", null);
        }
        seatMapper.deleteSeat(seatId);
        return new ApiResponse<>("SUCCESS", "Seat deleted successfully.", null);
    }

    // ================= BOOK SEAT =================
    public ApiResponse<Void> bookSeat(Long tripId, String seatNo) {
        int updated = seatMapper.updateSeatStatus(tripId, seatNo, 1); // 1 = BOOKED
        if (updated == 0) {
            return new ApiResponse<>("FAILURE", "Seat booking failed. Seat may already be booked.", null);
        }
        return new ApiResponse<>("SUCCESS", "Seat booked successfully.", null);
    }

    // ================= CANCEL SEAT =================
    public ApiResponse<Void> cancelSeat(Long tripId, String seatNo) {
        int updated = seatMapper.updateSeatStatus(tripId, seatNo, 0); // 0 = AVAILABLE
        if (updated == 0) {
            return new ApiResponse<>("FAILURE", "Seat cancel failed. Seat may already be available.", null);
        }
        return new ApiResponse<>("SUCCESS", "Seat cancelled successfully.", null);
    }

    // ================= GET SEATS BY TRIP =================
    public ApiResponse<List<Seat>> getSeatsByTrip(Long tripId) {
        List<Seat> seats = seatMapper.findSeatsByTripId(tripId);
        if (seats.isEmpty()) {
            return new ApiResponse<>("SUCCESS", "No seats found for this trip.", seats);
        }
        return new ApiResponse<>("SUCCESS", "Seats retrieved successfully.", seats);
    }
}