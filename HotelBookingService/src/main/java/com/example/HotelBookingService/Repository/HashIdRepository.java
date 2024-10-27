package com.example.HotelBookingService.Repository;
import com.example.HotelBookingService.Entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Repository
public interface HashIdRepository extends JpaRepository<RoomAvailability, Long> {
    List<RoomAvailability> findByBookingId(String bookingId);
}
