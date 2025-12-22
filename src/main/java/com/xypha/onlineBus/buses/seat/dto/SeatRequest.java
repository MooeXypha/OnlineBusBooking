package com.xypha.onlineBus.buses.seat.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SeatRequest {
    @NotNull(message = "Trip ID cannot be null")
    private Long tripId;


    @NotNull(message = "Seat numbers cannot be null")
    private List<String> selectedSeats;


    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }


    public List<String> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<String> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }
}


