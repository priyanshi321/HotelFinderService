package com.example.HotelBookingService.Controller;
import com.example.HotelBookingService.Entity.HotelBooking;
import com.example.HotelBookingService.Entity.HotelDetails;
import com.example.HotelBookingService.ExceptionHandler.HotelBookingValidationException;
import com.example.HotelBookingService.Service.HotelBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class HotelBookingController {

    @Autowired
    private HotelBookingService hotelBookingService;

    @PostMapping
    public ResponseEntity<HotelBooking> createBooking(@RequestBody HotelBooking booking) throws Exception {
        HotelBooking savedBooking = hotelBookingService.createBooking(booking);
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }
    @GetMapping("/available")
    public ResponseEntity<?> findAvailableHotelsByLocationAndDates(
            @RequestParam String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkinDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkoutDate) {
        if (location == null || location.isEmpty()) {
            throw new HotelBookingValidationException("Location cannot be null or empty.");
        }
        if (checkinDate == null) {
            throw new HotelBookingValidationException("Check-in date cannot be null.");
        }
        if (checkoutDate == null) {
            throw new HotelBookingValidationException("Check-out date cannot be null.");
        }
        if (!checkoutDate.isAfter(checkinDate)) {
            throw new HotelBookingValidationException("Check-out date must be after check-in date.");
        }
        try {
            List<HotelDetails> availableHotels = hotelBookingService.findAvailableHotelsByLocationAndDates(location, checkinDate, checkoutDate);
            if (availableHotels.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "No hotels available for the specified location and dates."));
            }
            return ResponseEntity.ok(availableHotels);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message",e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Unexpected error fetching available hotels."));
        }

    }
    @GetMapping
    public ResponseEntity<List<HotelBooking>> getAllBookings() {
        List<HotelBooking> bookings = hotelBookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<HotelBooking> getBookingById(@PathVariable String id) {
        try {
            HotelBooking booking = hotelBookingService.getBookingById(id);
            return new ResponseEntity<>(booking, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable String bookingId) {
        String message = hotelBookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(message);
    }

}
