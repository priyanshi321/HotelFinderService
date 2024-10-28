 Hotel Booking System
The Hotel Booking System is a simplified platform allowing users to book hotel rooms, check availability, retrieve booking details, and cancel bookings.
The system manages hotel data, room availability, and bookings, with concurrency controls to prevent overbooking

Services : 
HotelDetailsService
The HotelDetailsService manages hotel creation 
Hotel Entity: It has unique HotelIDs , charge, Location, numberOfRooms for each hotel .
Room Management: For each hotel, generates room IDs based on the numberOfRooms. Rooms are stored with unique roomIds linked to a hotelId as a foreign key.

Validations :
perfomed various validations for example checkinDate should be earlier than checkoutDate,Ensures the hotelId exists before creating bookings,Checks for non-null values  and many more required validations.

HotelBookingService
The HotelBookingService is responsible for all booking-related operations:
Create Booking: Creates a booking for a specified hotel'Id, check-in and check-out dates,Location, number of rooms, custermerId, BookingStatus, PaymentStatus, BookingId
Check Availability: Returns list of available hotelsDetails for a specific location, check-in date, and check-out dates.
Retrieve Booking: Fetches booking details by booking ID.
Cancel Booking: Cancels a booking by its BookingID, making the room(s) available for future bookings.

RoomAvailability
The RoomAvailability entity handles room availability by storing unique hash IDs:
Hash ID (Unique Constraint): Combines hotelId, roomId, and booking dates(dates between check-in and check-out dates) (e.g., hotelId-roomId-date) to prevent overlapping bookings for the same room on the same date range.
This unique constraint enforces room availability and ensures that each room can only be booked by one customer at any given date.

Concurrency Management
Concurrent bookings: If two users attempt to book the same room at the same time, only one should succeed for this we have done below changes :
The system uses locks and unique constraints to handle concurrent booking requests:
Locks: used in check method of createBooking where we are generating hashId that Prevent simultaneous updates on the same room availability records, ensuring only one booking request succeeds for a specific room on overlapping dates.
Unique Constraints: The unique hash ID for each room booking prevents duplicate bookings for the same room and dates.

Partial availability: If there are fewer rooms available than requested, the booking should fail with an appropriate error message
so In this also if roomtoBook is greater than totalRoomsAvailable that Booking is not done and it returns approperite message("Not enough rooms available for Booking !!!")
