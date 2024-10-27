package com.example.HotelBookingService.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "hotel_booking")
public class HotelBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @JsonIgnore
    private Long id;

    @Column(name = "booking_id", nullable = false, unique = true)
    private String bookingId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "checkin_date", nullable = false)
    private LocalDateTime checkinDate;

    @Column(name = "checkout_date", nullable = false)
    private LocalDateTime checkoutDate;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "number_of_rooms", nullable = false)
    private int numberOfRooms;

    @Column(name = "booking_status", nullable = false)
    private String bookingStatus;

    @Override
    public String toString() {
        return "HotelBooking{" +
                "bookingId=" + bookingId +
                ", customerId=" + customerId +
                ", checkinDate=" + checkinDate +
                ", checkoutDate=" + checkoutDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", hotelId=" + hotelId +
                ", location='" + location + '\'' +
                ", numberOfRooms=" + numberOfRooms +
                ", bookingStatus='" + bookingStatus + '\'' +
                '}';
    }
}
