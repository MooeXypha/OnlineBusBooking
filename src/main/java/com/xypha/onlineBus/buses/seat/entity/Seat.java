package com.xypha.onlineBus.buses.seat.entity;

public class Seat {
    private Long id;
    private Long tripId;
    private String seatNo;
    private int status;

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

    public String getSeatNo() {
        return seatNo;
    }
    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public void add(Seat seat) {
    }
}
