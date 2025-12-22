package com.xypha.onlineBus.booking.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class BookingRequest {

    @NotNull (message = "TripId cannot be null")
    private Long tripId;

    @NotNull(message = "SeatNumber must be filled")
    private List<String> seatNumbers;



    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }
}
