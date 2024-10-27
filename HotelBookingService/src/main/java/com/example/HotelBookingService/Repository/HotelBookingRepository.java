package com.example.HotelBookingService.Repository;
import com.example.HotelBookingService.Entity.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
    Optional<HotelBooking> findByBookingId(String bookingId);
    List<HotelBooking> findByHotelIdIn(List<Long> hotelIds);
}

