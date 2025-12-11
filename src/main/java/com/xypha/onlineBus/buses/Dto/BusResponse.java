package com.xypha.onlineBus.buses.Dto;

import com.xypha.onlineBus.buses.busType.dto.BusTypeResponse;
import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;

import java.time.LocalDateTime;

public class BusResponse {

        private Long id;
        private String busNumber;
        private BusTypeResponse busType;
        private Integer totalSeats;


        private String imgUrl;
        private String description;

        private Double pricePerKm;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;








    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public BusTypeResponse getBusType() {
        return busType;
    }

    public void setBusType(BusTypeResponse busType) {
        this.busType = busType;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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


    public Double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(Double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }


}



