package com.xypha.onlineBus.trip.dto;

import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.routes.Dto.RouteResponse;

import java.time.LocalDateTime;

public class TripResponse {

    private Long id;
    private Long routeId;
    private Long busId;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private Double fare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private RouteResponse route;
    private BusResponse bus;

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
}