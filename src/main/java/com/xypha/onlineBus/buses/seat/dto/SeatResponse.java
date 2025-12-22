package com.xypha.onlineBus.buses.seat.dto;

import java.util.List;

public class SeatResponse {

    private Long id;
    private Long tripId;
    private String selectedSeats;
    private int status;

    private boolean selected;

    public SeatResponse(Long id, Long tripId, String seatNo, int i) {
    }

    public SeatResponse() {

    }

    public SeatResponse(Long tripId, List<String> availableSeats) {
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(String selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
