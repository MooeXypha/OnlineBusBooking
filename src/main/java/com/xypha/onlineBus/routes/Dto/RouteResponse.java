package com.xypha.onlineBus.routes.Dto;

import com.xypha.onlineBus.buses.Dto.BusResponse;

import java.time.LocalDateTime;


public class RouteResponse {

    private Long id;
    private String source;
    private String destination;

    private Double distance;

    private Integer duration;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;




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



    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
}
