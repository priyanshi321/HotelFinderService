package com.example.HotelBookingService.Controller;
import com.example.HotelBookingService.Entity.HotelDetails;
import com.example.HotelBookingService.Service.HotelDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelDetailsController {

    @Autowired
    private HotelDetailsService hotelDetailsService;

    @PostMapping
    public ResponseEntity<HotelDetails> createHotel(@RequestBody HotelDetails hotelDetails) {
        HotelDetails createdHotel = hotelDetailsService.createHotelWithRooms(hotelDetails);
       return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HotelDetails>> getAllHotels() {
        List<HotelDetails> hotels = hotelDetailsService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDetails> getHotelById(@PathVariable Long hotelId) {
        HotelDetails hotel = hotelDetailsService.getHotelById(hotelId);
        if (hotel != null) {
            return new ResponseEntity<>(hotel, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
