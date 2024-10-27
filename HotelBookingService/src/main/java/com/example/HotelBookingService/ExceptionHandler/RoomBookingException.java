package com.example.HotelBookingService.ExceptionHandler;

public class RoomBookingException extends RuntimeException {
    public RoomBookingException(String message) {
        super(message);
    }
}