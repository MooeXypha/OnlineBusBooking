package com.xypha.onlineBus.booking.Entity;


import java.time.LocalDateTime;

public class Booking {

    private Long id;
    private Long route_id;
    private String passenger_name;
    private String passenger_phone;
    private Integer seat_booked;
    private Double total_price;
    private LocalDateTime bookingTime;

}
