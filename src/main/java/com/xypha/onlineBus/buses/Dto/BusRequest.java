package com.xypha.onlineBus.buses.Dto;

import jakarta.validation.constraints.*;

public class BusRequest {


    @NotBlank(message = "Bus number is required")
    @Pattern(
            regexp = "^(?!\\d{2})[A-Z\\d]{1,2}/\\d{4}$",
            message = "Invalid Myanmar bus number format. Example: 5I/2108 or AB/1234"
    )
    private String busNumber;


    private Long BusTypeId;

    @NotNull
    @Min(10)
    private Integer totalSeats;


    @NotNull (message = "Image URL is required")
    private String imgUrl;
    @NotBlank
    private String description;

    @NotNull(message = "Price per km is required")
    private Double pricePerKm;



    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public Long getBusTypeId() {
        return BusTypeId;
    }

    public void setBusTypeId(Long busTypeId) {
        BusTypeId = busTypeId;
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

    public Double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(Double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }
}
