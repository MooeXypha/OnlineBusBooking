package com.xypha.onlineBus.trip.entity;

import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.routes.Entity.Route;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.EventObject;
import java.util.Locale;

public class Trip {
    private Long id;
    @NotNull(message = "Route ID is required")
    private Long routeId;
    @NotNull(message = "Bus ID is required")
    private Long busId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;
    @NotNull(message = "Assistant ID is required")
    private Long assistantId;
    @NotNull(message = "Departure date is required")

    private LocalDateTime departureDate;
    @NotNull(message = "Arrival date is required")
    private LocalDateTime arrivalDate;

    private String duration;

    private Double fare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Trip trip;

    private Route route;

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Route getRoute() {
        return route;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(Long assistantId) {
        this.assistantId = assistantId;
    }


    public void setRoute(Route route) {
    }

    public void setBus(Bus bus) {
    }
}
