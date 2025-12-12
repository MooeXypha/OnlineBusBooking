package com.xypha.onlineBus.trip.dto;

import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;

import java.time.LocalDateTime;

public class TripResponse {

    private Long id;
    private Long routeId;
    private Long busId;

    private Long driverId;
    private Long assistantId;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private Double fare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String duration;
    private RouteResponse route;
    private BusResponse bus;

    private DriverResponse driver;
    private AssistantResponse assistant;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public LocalDateTime getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDateTime departureDate) { this.departureDate = departureDate; }

    public LocalDateTime getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(LocalDateTime arrivalDate) { this.arrivalDate = arrivalDate; }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getFare() { return fare; }
    public void setFare(Double fare) { this.fare = fare; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

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
}