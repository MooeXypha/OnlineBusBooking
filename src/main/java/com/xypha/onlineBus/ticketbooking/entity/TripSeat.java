package com.xypha.onlineBus.ticketbooking.entity;

import java.time.OffsetDateTime;

public class TripSeat {
    private Long id;
    private Long tripId;
    private Long seatId;
    private String seatCode;
    private Boolean isReserved;
    private OffsetDateTime reservedUntil;

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

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public Boolean getReserved() {
        return isReserved;
    }

    public void setReserved(Boolean reserved) {
        isReserved = reserved;
    }

    public OffsetDateTime getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(OffsetDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
    }
}
