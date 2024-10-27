package com.example.HotelBookingService.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "room")
public class RoomIdDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelDetails hotelDetails;

    @Override
    public String toString() {
        return "RoomIdDetails{" +
                "roomId='" + roomId + '\'' +
                '}';
    }
}
