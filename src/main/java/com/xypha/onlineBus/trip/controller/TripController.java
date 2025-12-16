package com.xypha.onlineBus.trip.controller;

import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import com.xypha.onlineBus.trip.dto.TripRequest;
import com.xypha.onlineBus.trip.dto.TripResponse;
import com.xypha.onlineBus.trip.entity.Trip;
import com.xypha.onlineBus.trip.services.TripServiceImpl;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping ("/api/trip")
public class TripController {

    @Autowired
    private TripServiceImpl tripService;


    @PostMapping
    public ApiResponse<TripResponse> createTrip(
            @Valid @RequestBody TripRequest tripRequest){
        return tripService.createTrip(tripRequest);
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<PaginatedResponse<TripResponse>>> getAllTrips(
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue = "10") int limit
    ){
        return ResponseEntity.ok(tripService.getAllTrips(offset, limit));
    }

    @PutMapping ("/{id}")
    public ApiResponse<TripResponse> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody TripRequest tripRequest){
        return tripService.updateTrip(id, tripRequest);
    }

    @GetMapping("/{id}")
    public ApiResponse<TripResponse> getTripById(
            @PathVariable Long id){
        return tripService.getTripById(id);
    }

    @DeleteMapping ("/{id}")
    public ApiResponse<Void> deleteTrip(
            @PathVariable Long id){
        return tripService.deleteTrip(id);
    }

    @GetMapping("/search")
    public ApiResponse<List<TripResponse>> searchTrips (
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false)
            @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) String departureDateStr)

            {
                LocalDate departureDate = null;

                if (departureDateStr != null && !departureDateStr.isBlank()) {
                    departureDateStr = departureDateStr.trim();
                    departureDate = LocalDate.parse(departureDateStr);
                }
                return tripService.searchTrips(source, destination, departureDate);
    }

    @GetMapping("/count")
    public ApiResponse<Integer> countTrips (
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date
    ){
        return tripService.countTripsByDepartureDate(date);
    }


}
