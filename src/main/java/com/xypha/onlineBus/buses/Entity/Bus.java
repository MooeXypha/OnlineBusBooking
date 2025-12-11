package com.xypha.onlineBus.buses.Entity;


import com.xypha.onlineBus.buses.busType.dto.BusTypeResponse;
import com.xypha.onlineBus.buses.busType.entity.BusType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;


public class Bus {

    private long id;

    @NotNull(message = "Bus number cannot be empty")
    private String busNumber;

    @NotNull(message = "Bus type cannot be empty")
    private BusType busType;

    @NotNull(message = "Total seats cannot be empty")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;



    private String imgUrl;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Distance cannot be empty")
    @Min(value = 1, message = "Distance must be at least 1km")
    private Double pricePerKm;
    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public BusType getBusType() {
        return busType;
    }

    public void setBusType(BusType busType) {
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


