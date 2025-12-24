package com.xypha.onlineBus.buses.seat.controller;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.buses.seat.dto.SeatRequest;
import com.xypha.onlineBus.buses.seat.dto.SeatResponse;
import com.xypha.onlineBus.buses.seat.entity.Seat;
import com.xypha.onlineBus.buses.seat.services.SeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat")
public class SeatController {
    private final SeatService seatService;


    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    //Generate seat for a trip


    // ================= CREATE SEAT =================
    // ================= GENERATE SEATS FOR TRIP =================
    // Usually called when trip is created (Admin)
    @PostMapping("/generate")
    public ApiResponse<Void> generateSeats(
            @RequestParam Long tripId,
            @RequestParam Long busId
    ) {
        seatService.generateSeatsForTrip(tripId, busId);
        return new ApiResponse<>("SUCCESS", "Seats generated successfully", null);
    }


    // ================= BOOK MULTIPLE SEATS =================
    @PostMapping("/book")
    public ApiResponse<Void> bookSeats(@RequestBody SeatRequest request) {
        if (request.getSelectedSeats() == null ||
        request.getSelectedSeats().isEmpty()){
            return new ApiResponse<>("FAILURE","Seat list is required", null);
        }
        return seatService.bookSeats(
                request.getTripId(),
                request.getSelectedSeats()
        );
    }

    // ================= CANCEL MULTIPLE SEATS =================
    @PostMapping("/cancel")
    public ApiResponse<Void> cancelSeats(@RequestBody SeatRequest request) {
        return seatService.cancelSeats(
                request.getTripId(),
                request.getSelectedSeats()
        );
    }

    // ================= GET SEAT BY ID =================
    @GetMapping("/{seatId}")
    public ApiResponse<SeatResponse> getSeatById(@PathVariable Long seatId) {
        return seatService.getSeatById(seatId);
    }

    // ================= GET SEATS BY TRIP =================
    @GetMapping("/trip/{tripId}")
    public ApiResponse<List<Seat>> getSeatsByTrip(@PathVariable Long tripId) {
        return seatService.getSeatsByTrip(tripId);
    }

    // ================= GET ALL SEATS (PAGINATED) =================
    @GetMapping("/paginated")
    public ApiResponse<PaginatedResponse<Seat>> getSeatsPaginated(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return seatService.getSeatsPaginated(offset, limit);
    }

    @GetMapping("/{tripId}/available")
    public ApiResponse<List<String>> getAvailableSeatByTripId (
            @PathVariable Long tripId
    ){
        return seatService.getAvailableSeatByTripId(tripId);
    }

    @GetMapping("/{tripId}/booked")
    public ApiResponse<List<String>> getBookedSeatByTripId (
            @PathVariable Long tripId
    ){
        return seatService.getBookedSeatByTripId(tripId);
    }


}