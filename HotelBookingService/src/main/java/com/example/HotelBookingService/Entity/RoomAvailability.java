package com.example.HotelBookingService.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@Table(name = "RoomAvailability", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hash_id"})
})
public class RoomAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "date", nullable = false)
    private LocalDate date;


    @Column(name = "hash_id", nullable = false,unique = true)
    private String hashId;
    @Column(name = "booking_id",nullable = false)
    private String bookingId;
    @Override
    public String toString() {
        return "RoomAvailability{" +
                "id=" + id +
                ", roomId='" + roomId + '\'' +
                ", date=" + date +
                ", hashId='" + hashId + '\'' +
                '}';
    }
}
