package com.example.HotelBookingService.ExceptionHandler;
public class HotelValidationException extends RuntimeException {
    public HotelValidationException(String message) {
        super(message);
    }
}

