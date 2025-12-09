package com.xypha.onlineBus.trip.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TripRequest {

    private long id;

    @NotNull
    private long routeId;

    @NotNull
    private long busId;

    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private LocalDateTime departureDate;

    @NotNull(message = "Arrival date is required")
    @Future(message = "Arrival date must be in the future")
    private LocalDateTime arrivalDate;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public long getBusId() {
        return busId;
    }

    public void setBusId(long busId) {
        this.busId = busId;
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


}
