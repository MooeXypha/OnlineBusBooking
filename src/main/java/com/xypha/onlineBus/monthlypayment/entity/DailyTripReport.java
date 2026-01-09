package com.xypha.onlineBus.monthlypayment.entity;

import java.time.LocalDateTime;

public class DailyTripReport {
    private Long tripId;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private String busNumber;
    private Long routeId;
    private Long sourceCityId;
    private Long destinationCityId;
    private String driverName;
    private String assistantName;
    private Integer totalSeatsSold;
    private Double totalRevenue;
    private Integer totalSeats;


    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getSourceCityId() {
        return sourceCityId;
    }

    public void setSourceCityId(Long sourceCityId) {
        this.sourceCityId = sourceCityId;
    }

    public Long getDestinationCityId() {
        return destinationCityId;
    }

    public void setDestinationCityId(Long destinationCityId) {
        this.destinationCityId = destinationCityId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }

    public Integer getTotalSeatsSold() {
        return totalSeatsSold;
    }

    public void setTotalSeatsSold(Integer totalSeatsSold) {
        this.totalSeatsSold = totalSeatsSold;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
}
