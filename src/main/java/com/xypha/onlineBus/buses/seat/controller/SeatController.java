package com.xypha.onlineBus.buses.seat.controller;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
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
    @PostMapping
    public ApiResponse<Seat> createSeat(@RequestBody Seat seat) {
        return seatService.createSeat(seat);
    }

    // ================= GET SEAT BY ID =================
    @GetMapping("/{seatId}")
    public ApiResponse<Seat> getSeatById(@PathVariable Long seatId) {
        return seatService.getSeatById(seatId);
    }

    // ================= GET ALL SEATS =================
//    @GetMapping
//    public ApiResponse<List<Seat>> getAllSeats() {
//        return seatService.getAllSeats();
//    }

    // ================= GET ALL SEATS PAGINATED =================
    @GetMapping("/paginated")
    public ApiResponse<PaginatedResponse<Seat>> getSeatsPaginated(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return seatService.getSeatsPaginated(offset, limit);
    }

    // ================= UPDATE SEAT =================
    @PutMapping("/{seatId}")
    public ApiResponse<Seat> updateSeat(@PathVariable Long seatId, @RequestBody Seat seat) {
        return seatService.updateSeat(seatId, seat);
    }

    // ================= DELETE SEAT =================
    @DeleteMapping("/{seatId}")
    public ApiResponse<Void> deleteSeat(@PathVariable Long seatId) {
        return seatService.deleteSeat(seatId);
    }

    // ================= GET SEATS BY TRIP =================
    @GetMapping("/trip/{tripId}")
    public ApiResponse<List<Seat>> getSeatsByTrip(@PathVariable Long tripId) {
        return seatService.getSeatsByTrip(tripId);
    }

    // ================= BOOK SEAT =================
    @PostMapping("/trip/{tripId}/book")
    public ApiResponse<Void> bookSeat(@PathVariable Long tripId, @RequestParam String seatNo) {
        return seatService.bookSeat(tripId, seatNo);
    }

    // ================= CANCEL SEAT =================
    @PostMapping("/trip/{tripId}/cancel")
    public ApiResponse<Void> cancelSeat(@PathVariable Long tripId, @RequestParam String seatNo) {
        return seatService.cancelSeat(tripId, seatNo);
    }
}