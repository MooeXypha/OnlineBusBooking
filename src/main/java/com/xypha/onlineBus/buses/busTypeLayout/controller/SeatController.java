package com.xypha.onlineBus.buses.busTypeLayout.controller;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.buses.busTypeLayout.entity.Seat;
import com.xypha.onlineBus.buses.busTypeLayout.services.SeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat")
public class SeatController {
    private final SeatService seatService;


    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    //Get seat for a trip
    @GetMapping("/trip/{tripId}")
    public ApiResponse<List<Seat>> getSeatByTrip (
            @PathVariable Long tripId
    ){
        return new ApiResponse<>("SUCCESS","Seats retrieved", seatService.getSeatsByTrip(tripId));
    }

    //Book seat
    @PostMapping("/trip/{tripId}/book")
    public ApiResponse<Void> bookSeat (@PathVariable Long tripId, @RequestParam String seatNumber){
        seatService.bookSeat(tripId, seatNumber);
        return new ApiResponse<>("SUCCESS", "Seat booked successfully", null);
    }

    //Avaliable seat
    @PostMapping("/trip/{tripId}/free")
    public ApiResponse<Void> freeSeat (@PathVariable Long tripId, @RequestParam String seatNumber){
        seatService.avaliavleSeat(tripId, seatNumber);
        return new ApiResponse<>("SUCCESS","Available seats retrieved successfully", null);
    }


}
