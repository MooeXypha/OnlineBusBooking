package com.xypha.onlineBus.trip.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class TripRequest {

    private long id;

    @NotNull
    private long routeId;

    @NotNull(message = "Bus ID is required")
    private long busId;

    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private OffsetDateTime departureDate;

    @NotNull(message = "Arrival date is required")
    @Future(message = "Arrival date must be in the future")
    private OffsetDateTime arrivalDate;


@NotNull(message = "Driver ID is required")
    private long driverId;
@NotNull(message = "Assistant ID is required")
    private long assistantId;


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

    public OffsetDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(OffsetDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public OffsetDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(OffsetDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public long getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(long assistantId) {
        this.assistantId = assistantId;
    }

    public void setDuration(String duration) {
    }

    public void setFare(double v) {
    }
}
