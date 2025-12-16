package com.xypha.onlineBus.buses.busType.dto;

import com.xypha.onlineBus.buses.services.Service;
import com.xypha.onlineBus.buses.services.ServiceResponse;

import java.time.LocalDateTime;
import java.util.List;

public class BusTypeResponse {

    private long id;
    private String name;

    private Integer seatPerRow;
    private List<ServiceResponse> services;  // This is the key part

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSeatPerRow() {
        return seatPerRow;
    }

    public void setSeatPerRow(Integer seatPerRow) {
        this.seatPerRow = seatPerRow;
    }

    public List<ServiceResponse> getServices() {
        return services;
    }

    public void setServices(List<ServiceResponse> services) {
        this.services = services;
    }
}
