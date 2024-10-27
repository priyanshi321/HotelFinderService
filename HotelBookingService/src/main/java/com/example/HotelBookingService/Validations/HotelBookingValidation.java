package com.example.HotelBookingService.Validations;
import com.example.HotelBookingService.Entity.HotelBooking;
import com.example.HotelBookingService.ExceptionHandler.HotelBookingValidationException;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class HotelBookingValidation {

    public void validateHotelBooking(HotelBooking hotelBooking) {
        List<String> errors = new ArrayList<>();

        if (hotelBooking.getCustomerId() == null) {
            errors.add("Customer ID cannot be null");
        }
        if (hotelBooking.getCustomerId() < 0) {
            errors.add("Customer ID cannot be negative");
        }
        if (hotelBooking.getCheckinDate() == null) {
            errors.add("Check-in date cannot be null");
        } else if (hotelBooking.getCheckinDate().isBefore(LocalDateTime.now())) {
            errors.add("Check-in date cannot be in the past");
        }
        if (hotelBooking.getCheckoutDate() == null) {
            errors.add("Checkout date cannot be null");
        } else if (hotelBooking.getCheckoutDate().isBefore(hotelBooking.getCheckinDate())) {
            errors.add("Checkout date must be after check-in date or Checkin date must be before check-out date");
        }

        if (hotelBooking.getHotelId() == null) {
            errors.add("Hotel ID cannot be null");
        } else if (hotelBooking.getHotelId() < 0) {
            errors.add("Hotel ID cannot be negative");
        }
        if (hotelBooking.getLocation() == null || hotelBooking.getLocation().isEmpty()) {
            errors.add("Location cannot be null or empty");
        }
        if (hotelBooking.getNumberOfRooms() <= 0) {
            errors.add("Number of rooms must be positive and greater than zero");
        }
        if (!errors.isEmpty()) {
            throw new HotelBookingValidationException(String.join("; ", errors));
        }
    }
}
