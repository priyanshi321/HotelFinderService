package com.example.HotelBookingService.Service;
import com.example.HotelBookingService.Entity.HotelDetails;
import com.example.HotelBookingService.Repository.HotelDetailsRepository;
import com.example.HotelBookingService.Validations.HotelDetailsValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelDetailsService {

    @Autowired
    private HotelDetailsRepository hotelDetailsRepository;



    public HotelDetails createHotelWithRooms(HotelDetails hotelDetails) {
        HotelDetailsValidation.validateHotelDetails(hotelDetails);
        hotelDetails.generateRoomIds();
        return hotelDetailsRepository.save(hotelDetails);
    }

    public List<HotelDetails> getAllHotels() {
        return hotelDetailsRepository.findAll();
    }

    public HotelDetails getHotelById(Long hotelId) {
        if (hotelId < 0) {
            throw new IllegalArgumentException("Hotel ID cannot be negative.");
        }
        HotelDetails hotelDetails = hotelDetailsRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + hotelId));

        return hotelDetails;
    }
}
