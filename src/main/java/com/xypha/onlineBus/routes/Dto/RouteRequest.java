package com.xypha.onlineBus.routes.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class RouteRequest {

    @NotNull(message = "Source city ID cannot be null")
    private Long sourceCityId;

    @NotNull(message = "Destination city ID cannot be null")
    private Long destinationCityId;

    public Long getSourceCityId() {
        return sourceCityId;
    }

    public void setSourceCityId(Long sourceCityId) {
        this.sourceCityId = sourceCityId;
    }

    public Long getDestinationCityId() {
        return destinationCityId;
    }

    public void setDestinationCityId(Long destinationCityId) {
        this.destinationCityId = destinationCityId;
    }
}
