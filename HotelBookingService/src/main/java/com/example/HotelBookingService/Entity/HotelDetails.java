package com.example.HotelBookingService.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "hotel_details")
public class HotelDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "number_of_rooms", nullable = false)
    private int numberOfRooms;

    @Column(name = "charge", nullable = false)
    private Double charge;

    @OneToMany(mappedBy = "hotelDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RoomIdDetails> rooms = new ArrayList<>();

    public void generateRoomIds() {
        rooms.clear();
        for (int i = 1; i <= numberOfRooms; i++) {
            RoomIdDetails room = new RoomIdDetails();
            room.setRoomId(Integer.toString(i));
            room.setHotelDetails(this);
            rooms.add(room);
        }
    }


    @Override
    public String toString() {
        return "HotelDetails{" +
                "hotelId=" + hotelId +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", numberOfRooms=" + numberOfRooms +
                ", charge=" + charge +
                ", rooms=" + rooms +
                '}';
    }
}
