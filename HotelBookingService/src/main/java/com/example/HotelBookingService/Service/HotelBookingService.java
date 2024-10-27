package com.example.HotelBookingService.Service;
import com.example.HotelBookingService.Entity.RoomAvailability;
import com.example.HotelBookingService.Entity.HotelBooking;
import com.example.HotelBookingService.Entity.HotelDetails;
import com.example.HotelBookingService.Entity.RoomIdDetails;
import com.example.HotelBookingService.ExceptionHandler.RoomBookingException;
import com.example.HotelBookingService.Repository.HashIdRepository;
import com.example.HotelBookingService.Repository.HotelBookingRepository;
import com.example.HotelBookingService.Repository.HotelDetailsRepository;
import com.example.HotelBookingService.Validations.HotelBookingValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class HotelBookingService {

    @Autowired
    private HotelBookingRepository hotelBookingRepository;
    @Autowired
    private HashIdRepository hashIdRepository;
    @Autowired
    private HotelDetailsService hotelDetailsService;
    @Autowired
    private HotelDetailsRepository hotelDetailsRepository;
    @Autowired
    private HotelBookingValidation hotelBookingValidation;
    private final Lock lock = new ReentrantLock();

    public HotelBooking createBooking(HotelBooking booking) {
        hotelBookingValidation.validateHotelBooking(booking);
        String bookingId = UUID.randomUUID().toString();
        booking.setBookingId(bookingId);
        check(booking,bookingId);
        booking.setPaymentStatus("Completed");
        booking.setBookingStatus("Confirmed");
        return saveBookingDetails(booking);
    }

    @Transactional
    public HotelBooking saveBookingDetails(HotelBooking booking) {
        booking.setPaymentStatus("Completed");
        booking.setBookingStatus("Confirmed");
        return hotelBookingRepository.save(booking);
    }

    @Transactional
    public  void  check(HotelBooking booking, String bookingId) {
        lock.lock();
        try {
            HotelDetails hotelDetails = hotelDetailsService.getHotelById(booking.getHotelId());
            if (hotelDetails == null) {
                throw new RoomBookingException("Hotel not found !!!");
            }
            List<RoomIdDetails> roomIdDetails = hotelDetails.getRooms();
            AtomicInteger roomsBooked = new AtomicInteger(0);
            for (RoomIdDetails room : roomIdDetails) {
                List<RoomAvailability> roomAvailabilityData = generateHashIds(
                        room.getRoomId(),
                        booking.getCheckinDate().toLocalDate(),
                        booking.getCheckoutDate().toLocalDate(),
                        booking.getHotelId(), bookingId
                );
                int bookingDays = 0;
                for (RoomAvailability roomAvailability : roomAvailabilityData) {
                    boolean saved = trySaveRoomAvailability(roomAvailability);
                    if (saved) {
                        bookingDays++;
                    }
                }
                long totalDaysBetween = ChronoUnit.DAYS.between(booking.getCheckinDate(), booking.getCheckoutDate());

                if (bookingDays == totalDaysBetween + 1) {
                    roomsBooked.incrementAndGet();
                }
                if (roomsBooked.get() == booking.getNumberOfRooms()) {
                    break;
                }
            }
            if (roomsBooked.get() < booking.getNumberOfRooms()) {
                deleteRoomAvailabilityEntriesByBookingId(bookingId);
                throw new RoomBookingException("Not enough rooms available !!!");
            }
        } finally {
            lock.unlock();
        }
    }
    @Transactional
    public boolean trySaveRoomAvailability(RoomAvailability roomAvailability) {
        try {
            hashIdRepository.save(roomAvailability);
            return true;
        }  catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    public void deleteRoomAvailabilityEntriesByBookingId(String bookingId) {
        List<RoomAvailability> roomAvailabilities = hashIdRepository.findByBookingId(bookingId);
        for (RoomAvailability roomAvailability : roomAvailabilities) {
            hashIdRepository.delete(roomAvailability);
        }
    }

    public List<HotelDetails> findAvailableHotelsByLocationAndDates(String location, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
        List<HotelDetails> hotelsInLocation = hotelDetailsRepository.findByLocation(location);
        if (hotelsInLocation.isEmpty()) {
            throw new RuntimeException("No hotels available for the specified location and dates");
        }
        List<Long> hotelIds = hotelsInLocation.stream()
                .map(HotelDetails::getHotelId)
                .collect(Collectors.toList());

        List<HotelBooking> allBookings = hotelBookingRepository.findByHotelIdIn(hotelIds);

        List<HotelDetails> availableHotels = new ArrayList<>();

        for (HotelDetails hotel : hotelsInLocation) {
            int totalRooms = hotel.getNumberOfRooms();
            int bookedRooms = 0;

            for (HotelBooking hotelBooking : allBookings) {
                if (hotelBooking.getHotelId().equals(hotel.getHotelId()) &&
                        (hotelBooking.getCheckinDate().isBefore(checkOutDate) && hotelBooking.getCheckoutDate().isAfter(checkInDate))) {
                    bookedRooms += hotelBooking.getNumberOfRooms();
                }
            }

            int remainingRooms = totalRooms - bookedRooms;

            if (remainingRooms > 0) {
                HotelDetails availableHotel = new HotelDetails();
                availableHotel.setHotelId(hotel.getHotelId());
                availableHotel.setLocation(hotel.getLocation());
                availableHotel.setStatus(hotel.getStatus());
                availableHotel.setCharge(hotel.getCharge());
                availableHotel.setNumberOfRooms(remainingRooms);
                availableHotels.add(availableHotel);
            }
        }

        return availableHotels;
    }

    public List<RoomAvailability> generateHashIds(String roomId, LocalDate checkInDate, LocalDate checkOutDate, Long hotelId,String bookingId) {
        List<RoomAvailability> roomAvailabilities = new ArrayList<>();
        for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
            RoomAvailability roomAvailability = new RoomAvailability();
            roomAvailability.setRoomId(roomId);
            roomAvailability.setDate(date);
            roomAvailability.setHotelId(hotelId);
            roomAvailability.setBookingId(bookingId);
            roomAvailability.setHashId(hotelId + "-" + roomId + "-" + date);
            roomAvailabilities.add(roomAvailability);
        }
        return roomAvailabilities;
    }

    public List<HotelBooking> getAllBookings() {
        List<HotelBooking> bookings = hotelBookingRepository.findAll();
        return bookings;
    }

    public HotelBooking getBookingById(String bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null");
        }

        Optional<HotelBooking> bookingOptional = hotelBookingRepository.findByBookingId(bookingId);
         if(!bookingOptional.isPresent())
         {
             throw new IllegalArgumentException("Booking ID " + bookingId + " does not exist.");
         }
        HotelBooking booking = bookingOptional.get();
        if ("Canceled".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException("Booking ID " + bookingId + " has  been canceled.");
        }
        return booking;
    }

    public String cancelBooking(String bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null");
        }
        Optional<HotelBooking> optionalBooking = hotelBookingRepository.findByBookingId(bookingId);
        if (!optionalBooking.isPresent()) {
            return "Booking ID " + bookingId + " does not exist.";
        }

        HotelBooking booking = optionalBooking.get();
        if ("Canceled".equals(booking.getBookingStatus())) {
            return "Booking ID " + bookingId + " has already been canceled.";
        }

        booking.setBookingStatus("Canceled");
        hotelBookingRepository.save(booking);
        deleteRoomAvailabilityEntriesByBookingId(bookingId);
        return "Booking canceled successfully.";
    }

}
