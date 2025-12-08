package com.xypha.onlineBus.routes.Dto;

import com.xypha.onlineBus.buses.Dto.BusResponse;

import java.time.LocalDateTime;


public class RouteResponse {

    private Long id;
    private String source;
    private String destination;

    private Double distance;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private BusResponse bus;
    //For the bus





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public void setSource(String source) {
        this.source = source;
    }



    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public BusResponse getBus() {
        return bus;
    }

    public void setBus(BusResponse bus) {
        this.bus = bus;
    }
}
