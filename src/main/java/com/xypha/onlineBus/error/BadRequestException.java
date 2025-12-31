package com.xypha.onlineBus.error;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }
}
