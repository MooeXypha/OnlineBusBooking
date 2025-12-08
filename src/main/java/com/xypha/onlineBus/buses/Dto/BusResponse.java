package com.xypha.onlineBus.buses.Dto;

import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;

import java.time.LocalDateTime;

public class BusResponse {

        private Long id;
        private String busNumber;
        private String busType;
        private Integer totalSeats;

        private Boolean hasAC;
        private Boolean hasWifi;

        private String imgUrl;
        private String description;

        private Double pricePerKm;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;



        private AssistantResponse assistant;
        private DriverResponse driver;





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

    public String getBusType(String busType) {
        return this.busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Boolean getHasAC() {
        return hasAC;
    }

    public void setHasAC(Boolean hasAC) {
        this.hasAC = hasAC;
    }

    public Boolean getHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(Boolean hasWifi) {
        this.hasWifi = hasWifi;
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

    public String getBusType() {
        return busType;
    }

    public Double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(Double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public AssistantResponse getAssistant() {
        return assistant;
    }

    public void setAssistant(AssistantResponse assistant) {
        this.assistant = assistant;
    }

    public DriverResponse getDriver() {
        return driver;
    }

    public void setDriver(DriverResponse driver) {
        this.driver = driver;
    }
}



