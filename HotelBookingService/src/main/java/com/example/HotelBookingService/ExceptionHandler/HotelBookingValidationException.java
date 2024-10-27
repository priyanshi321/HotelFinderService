package com.example.HotelBookingService.ExceptionHandler;

public class HotelBookingValidationException extends RuntimeException {
    public HotelBookingValidationException(String message) {
        super(message);
    }
}
