package com.example.HotelBookingService.Validations;
import com.example.HotelBookingService.Entity.HotelDetails;
import com.example.HotelBookingService.ExceptionHandler.HotelValidationException;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class HotelDetailsValidation {

    public static void validateHotelDetails(HotelDetails hotelDetails) {
        List<String> errors = new ArrayList<>();

        if (hotelDetails.getLocation() == null || hotelDetails.getLocation().isEmpty()) {
            errors.add("Location cannot be null or empty");
        }

        if (hotelDetails.getCharge() == null || hotelDetails.getCharge() <= 0) {
            errors.add("Charge must be a positive value and greater than zero");
        }

        if (hotelDetails.getStatus() == null || hotelDetails.getStatus().isEmpty()) {
            errors.add("Status cannot be null or empty");
        }

        if (hotelDetails.getNumberOfRooms() < 0) {
            errors.add("Number of rooms cannot be negative");
        }

        if (hotelDetails.getNumberOfRooms() == 0) {
            errors.add("Number of rooms should be greater than 0");
        }

        if (!errors.isEmpty()) {
            throw new HotelValidationException(String.join("; ", errors));
        }
    }
}
