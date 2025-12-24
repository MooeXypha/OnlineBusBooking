package com.xypha.onlineBus.buses.seat.services;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.buses.seat.dto.SeatResponse;
import com.xypha.onlineBus.buses.seat.entity.Seat;
import com.xypha.onlineBus.buses.seat.mapper.SeatMapper;
import com.xypha.onlineBus.buses.mapper.BusMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
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
    public void generateSeatsForTrip(Long tripId, Long busId) {
        if (seatMapper.countByTripId(tripId) > 0) {
            return; // Seats already generated for this trip
        }
        Bus bus = busMapper.getBusById(busId);
        if (bus == null){
            throw new IllegalArgumentException("Bus not found" + busId);
        }

        int totalSeats = bus.getTotalSeats();
        int seatsPerRow = bus.getBusType().getSeatPerRow();
        int rows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        List<Seat> seats = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            char row = (char) ('A' + r);
            for (int c = 1; c <= seatsPerRow; c++) {
                int seatNumber = r * seatsPerRow + c;
                if (seatNumber > totalSeats) {
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


    // ================= BOOK MULTIPLE SEATS =================
    @Transactional
    public ApiResponse<Void> bookSeats(Long tripId, List<String> selectedSeats) {

        if (selectedSeats == null || selectedSeats.isEmpty()) {
            return new ApiResponse<>("FAILURE", "No seats selected: " + selectedSeats, null);
        }

        // Book after validation
        for (String seatNo : selectedSeats) {
            Seat seat = seatMapper.getSeatByTripAndNo(tripId, seatNo.trim());
        if (seat == null) {
            return new ApiResponse<>("FAILURE", "Seat not found" + seatNo, null);
        }
        if (seat.getStatus() == 1) {
            return new ApiResponse<>("FAILURE", "Seat already booked:" +seatNo, null );
        }

            seatMapper.updateSeatStatus(seat.getId(), 1);
        }

        return new ApiResponse<>("SUCCESS", "Seats booked successfully", null);
    }




    // ================= CANCEL MULTIPLE SEATS =================
    @Transactional
    public ApiResponse<Void> cancelSeats(Long tripId, List<String> seatNos) {


        if (seatNos == null || seatNos.isEmpty()) {
            return new ApiResponse<>("FAILURE", "No seats provided", null);
        }

        for (String seatNo : seatNos) {
            Seat seat = seatMapper.getSeatByTripAndNo(tripId, seatNo.trim());

            if (seat == null) {
                return new ApiResponse<>("FAILURE",
                        "Seat not found: " + seatNo, null);
            }

            if (seat.getStatus() == 0) {
                return new ApiResponse<>("FAILURE",
                        "Seat already available: " + seatNo, null);
            }

            seatMapper.updateSeatStatus(seat.getId(), 0);
        }

        return new ApiResponse<>("SUCCESS", "Seats cancelled successfully", null);
    }

    // ================= GET SEAT BY ID =================
    public ApiResponse<SeatResponse> getSeatById(Long id) {

        Seat seat = seatMapper.getSeatById(id);
        if (seat == null)
            return new ApiResponse<>("NOT_FOUND", "Seat not found", null);

        SeatResponse resp = new SeatResponse(
                seat.getId(),
                seat.getTripId(),
                seat.getSeatNo(),
                seat.getStatus()
        );

        return new ApiResponse<>("SUCCESS", "Seat retrieved", resp);
    }

    // ================= GET SEATS BY TRIP =================
    public ApiResponse<List<Seat>> getSeatsByTrip(Long tripId) {
        return new ApiResponse<>(
                "SUCCESS",
                "Seats retrieved",
                seatMapper.findSeatsByTripId(tripId)
        );
    }

    // ================= PAGINATION =================
    public ApiResponse<PaginatedResponse<Seat>> getSeatsPaginated(int offset, int limit) {

        List<Seat> seats = seatMapper.getAllPaginated(offset, limit);
        int total = seatMapper.countSeats();

        return new ApiResponse<>(
                "SUCCESS",
                "Seats retrieved",
                new PaginatedResponse<>(offset, limit, total, seats)
        );
    }


    public ApiResponse<List<String>> getAvailableSeatByTripId (
            Long tripId
    ){
       List<String> availableSeats = seatMapper.findSeatsByTripId(tripId).stream()
               .filter(seat -> seat.getStatus() == 0)
               .map(Seat::getSeatNo)
               .toList();

       SeatResponse response = new SeatResponse(tripId, availableSeats);
       return new ApiResponse<>(
               "SUCCESS",
               "Available seats retrieved from: " + tripId,
               availableSeats
       );
    }

    public ApiResponse<List<String>> getBookedSeatByTripId(
            Long tripId
    ){
        List<String> bookedSeats = seatMapper.findSeatsByTripId(tripId).stream()
                .filter(seat -> seat.getStatus() == 1)
                .map(Seat::getSeatNo)
                .toList();
        return new ApiResponse<>("SUCCESS",
                "Booked seats retrieved from: " + tripId,
                bookedSeats);
    }

    @Transactional
    public boolean cancelSeat (Long seatId){
        Seat seat = seatMapper.getSeatById(seatId);
        if (seat == null){
            return false;
        }
        if (seat.getStatus() == 2 ){
            return false;
        }

        seat.setStatus(0);
        seatMapper.updateSeat(seat);
        return true;
    }

    @Transactional
    public int cancelSeats (List<Long> seatIds){
        int canceled = 0;
        for (Long seatId : seatIds){
            if (cancelSeat(seatId)){
                canceled++;
            }
        }
    return canceled;
    }


}