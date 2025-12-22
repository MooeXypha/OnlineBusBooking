package com.xypha.onlineBus.buses.seat.dto;

import java.util.List;

public class SeatListResponse {

    private Long tripId;
    private List<String> seats;

    public SeatListResponse(Long tripId, List<String> seats) {
        this.tripId = tripId;
        this.seats = seats;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }
}

