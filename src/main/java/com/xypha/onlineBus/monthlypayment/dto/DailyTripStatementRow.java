package com.xypha.onlineBus.monthlypayment.dto;

public class DailyTripStatementRow {
    private Long tripId;
    private Long routeId;
    private Long busId;

    private int seatSold;
    private int totalSeats;

    private double tripRevenue;
    private double loadFactor;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public int getSeatSold() {
        return seatSold;
    }

    public void setSeatSold(int seatSold) {
        this.seatSold = seatSold;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public double getTripRevenue() {
        return tripRevenue;
    }

    public void setTripRevenue(double tripRevenue) {
        this.tripRevenue = tripRevenue;
    }

    public double getLoadFactor() {
        return loadFactor;
    }

    public void setLoadFactor(double loadFactor) {
        this.loadFactor = loadFactor;
    }
}
