package com.xypha.onlineBus.trip.dto;

import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class TripResponse {

    private Long id;
    private Long routeId;
    private Long busId;

    private Long driverId;
    private Long assistantId;
    private OffsetDateTime departureDate;
    private OffsetDateTime arrivalDate;
    private Double fare;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private String duration;
    private RouteResponse route;
    private BusResponse bus;

    private DriverResponse driver;
    private AssistantResponse assistant;

    private String departureTime;
    private String arrivalTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getFare() { return fare; }
    public void setFare(Double fare) { this.fare = fare; }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public RouteResponse getRoute() { return route; }
    public void setRoute(RouteResponse route) { this.route = route; }

    public BusResponse getBus() { return bus; }
    public void setBus(BusResponse bus) { this.bus = bus; }

    public DriverResponse getDriver() {
        return driver;
    }

    public void setDriver(DriverResponse driver) {
        this.driver = driver;
    }

    public AssistantResponse getAssistant() {
        return assistant;
    }

    public void setAssistant(AssistantResponse assistant) {
        this.assistant = assistant;
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

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}