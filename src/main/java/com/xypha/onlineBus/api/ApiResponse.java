package com.xypha.onlineBus.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.token.dto.RefreshTokenResponse;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

@JsonInclude (JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String status;
    private String message;
    private LocalDateTime timestamp;
    private T payload;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(String status, String message, T payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
        this.timestamp = LocalDateTime.now(); // automatically set timestamp
    }

    public ApiResponse(boolean b, String token_refreshed_successfully, RefreshTokenResponse responseData) {
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
