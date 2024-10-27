package com.example.HotelBookingService.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.HotelBookingService.Entity.HotelDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelDetailsRepository extends JpaRepository<HotelDetails, Long> {
    List<HotelDetails> findByLocation(String location);
}
