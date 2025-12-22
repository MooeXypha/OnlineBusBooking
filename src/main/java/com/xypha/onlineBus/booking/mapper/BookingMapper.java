package com.xypha.onlineBus.booking.mapper;

import com.xypha.onlineBus.booking.entity.Booking;
import org.apache.ibatis.annotations.*;

import java.awt.*;
import java.util.List;

@Mapper
public interface BookingMapper {

    @Insert("""
            INSERT INTO booking (trip_id, user_id, booking_code, status, total_amount, created_at, updated_at)
            VALUES (#{tripId}, #{userId}, #{bookingCode}, #{status}, #{totalAmount}, NOW(), NOW())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createBooking(Booking booking);

    @Insert("""
            INSERT INTO booking_seat (booking_id, seat_id, trip_id, created_at, updated_at)
            VALUES (#{bookingId}, #{seatNo}, #{tripId}, NOW(), NOW())
            """)
    void createBookingSeat (@Param("bookingId") Long bookingId,
                            @Param("seatNo") Long seatId,
                            @Param("tripId") Long tripId);

    @Select("""
            SELECT * FROM booking 
            WHERE booking_code = #{bookingCode}
            """)
    Booking getByBookingCode (String bookingCode);

    @Update("""
            UPDATE booking SET status = #{status}, updated_at = NOW()
            WHERE booking_code = #{bookingCode}
            """)
    int updateStatus (
            @Param("bookingCode") String bookingCode,
            @Param("status") String status
    );

    @Select("""
            SELECT COUNT (*) FROM booking 
            WHERE booking_code = #{bookingCode}
            """)
    int countByBookingCode (String bookingCode);

    @Select("""
            SELECT seat_id FROM booking_seat WHERE booking_id = #{bookingId}
            """)
    List<Long> getSeatIdsByBookingId (@Param("bookingId") Long bookingId);

    @Select("""
            SELECT s.seat_no
            FROM seat s
            JOIN booking_seat bs ON s.id = bs.seat_id
            WHERE bs.booking_id = #{bookingId}
            """)
    List<String> getSeatNumbersByBookingId (@Param("bookingId") Long bookingId);



    void createBooking(Long id, Long seatId);
}
