package com.example.HotelBookingService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.HotelBookingService.Entity.HotelBooking;
import com.example.HotelBookingService.Entity.HotelDetails;
import com.example.HotelBookingService.ExceptionHandler.RoomBookingException;
import com.example.HotelBookingService.Repository.HashIdRepository;
import com.example.HotelBookingService.Repository.HotelBookingRepository;
import com.example.HotelBookingService.Service.HotelBookingService;
import com.example.HotelBookingService.Service.HotelDetailsService;
import com.example.HotelBookingService.Validations.HotelBookingValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@AutoConfigureMockMvc
public class HotelBookingServiceApplicationTests {

    @Autowired
    private HotelBookingService hotelBookingService; // Autowiring the service

    @Mock
    private HotelDetailsService hotelDetailsService; // Mocking the HotelDetailsService

    @Mock
    private HotelBookingValidation hotelBookingValidation; // Mocking the HotelBookingValidation

    @Autowired
    private HotelBookingRepository hotelBookingRepository; // Autowiring the repository

    @Autowired
    private HashIdRepository hashIdRepository; // Autowiring the HashIdRepository

    private HotelBooking booking;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); 
        booking = new HotelBooking();
        booking.setHotelId(1L);
        booking.setLocation("Bangalore");
        booking.setCustomerId(156L);
        booking.setBookingId("7a133b7b-7c65-410c-8c3f-061498892efa");
        booking.setCheckinDate(LocalDateTime.parse("2024-11-04T14:00:00"));
        booking.setCheckoutDate(LocalDateTime.parse("2024-11-07T14:00:00"));
        booking.setNumberOfRooms(3);

        HotelDetails hotelDetails = new HotelDetails();
       hotelDetails.setHotelId(1L);
       hotelDetails.setCharge(234D);
       hotelDetails.setLocation("Bangalore");
       hotelDetails.setStatus("Available");
       hotelDetails.setNumberOfRooms(3);
       hotelDetails.generateRoomIds();

        Mocking the hotelDetailsService to return the hotel details when requested
        when(hotelDetailsService.getHotelById(1L)).thenReturn(hotelDetails);
    }

    @Test
    public void testConcurrentBooking() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successfulBookings = new AtomicInteger(0);

        System.out.println("Starting test for concurrent booking");

        Thread thread1 = new Thread(() -> {
            try {
                latch.await();
                hotelBookingService.createBooking(booking);
                successfulBookings.incrementAndGet();
            } catch (RoomBookingException | InterruptedException e) {
                System.out.println("Thread 1 failed: " + e.getMessage());
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                latch.await();
                hotelBookingService.createBooking(booking);
                successfulBookings.incrementAndGet();
            } catch (RoomBookingException | InterruptedException e) {
                System.out.println("Thread 2 failed: " + e.getMessage());
            }
        });

        thread1.start();
        thread2.start();
        latch.countDown();

        thread1.join();
        thread2.join();

        System. out.println("Both threads completed.");
        System.out.println("Total successful bookings: " + successfulBookings.get());
        assertEquals(1, successfulBookings.get(), "Only one booking should succeed due to concurrency constraints.");
        assertEquals(1, hotelBookingRepository.count(), "Repository should contain exactly one booking.");
    }
}
