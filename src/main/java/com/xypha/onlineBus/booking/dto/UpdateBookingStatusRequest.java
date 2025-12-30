package com.xypha.onlineBus.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateBookingStatusRequest {

    @NotBlank(message = "Booking code is required")
    private String bookingCode;

    @NotBlank(message = "Status is required")
    private String status;

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
