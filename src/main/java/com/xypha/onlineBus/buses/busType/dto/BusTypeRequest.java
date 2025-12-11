package com.xypha.onlineBus.buses.busType.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BusTypeRequest {

    @NotNull
    private String name;

    private List<Long> serviceIds;

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
