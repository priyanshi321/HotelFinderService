package com.example.HotelBookingService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.HotelBookingService.Entity.HotelBooking;
import com.example.HotelBookingService.Entity.HotelDetails;
import com.example.HotelBookingService.ExceptionHandler.RoomBookingException;
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
    private HotelBookingService hotelBookingService;

    @Autowired
    private HotelDetailsService hotelDetailsService;

    @Mock
    private HotelBookingValidation hotelBookingValidation;

    @Autowired
    private HotelBookingRepository hotelBookingRepository;

    private HotelBooking booking;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        HotelDetails hotelDetails = new HotelDetails();
        hotelDetails.setHotelId(1L);
        hotelDetails.setCharge(234D);
        hotelDetails.setLocation("Bangalore");
        hotelDetails.setStatus("Available");
        hotelDetails.setNumberOfRooms(3);
        hotelDetailsService.createHotelWithRooms(hotelDetails);

    }



    @Test
    public void testConcurrentBooking() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successfulBookings = new AtomicInteger(0);
        Long hotelId = 1L;
        HotelBooking bookingRequest1 = createBookingRequest(hotelId, 3, 1L, "Bangalore");
        HotelBooking bookingRequest2 = createBookingRequest(hotelId, 3, 2L, "Bangalore");

        System.out.println("Starting test for concurrent booking");

        Thread thread1 = new Thread(() -> {
            try {
                latch.await();
                hotelBookingService.createBooking(bookingRequest1);
                successfulBookings.incrementAndGet();
            } catch (RoomBookingException | InterruptedException e) {
                System.out.println("Thread 1 failed: " + e.getMessage());
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                latch.await();
                hotelBookingService.createBooking(bookingRequest2);
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
    private HotelBooking createBookingRequest(Long hotelId, int numberOfRooms, Long customerId, String location) {
        HotelBooking booking = new HotelBooking();
        booking.setHotelId(hotelId);
        booking.setNumberOfRooms(numberOfRooms);
        LocalDateTime checkinDate = LocalDateTime.parse("2024-11-04T14:00:00");
        LocalDateTime checkoutDate = checkinDate.plusDays(1);
        booking.setCustomerId(customerId);
        booking.setLocation(location);
        booking.setCheckinDate(checkinDate);
        booking.setCheckoutDate(checkoutDate);
        booking.setPaymentStatus("Pending");
        booking.setBookingStatus("Pending");
        System.out.printf("Created booking request: %s%n", booking);
        return booking;
    }
}
