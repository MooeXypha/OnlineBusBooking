package com.xypha.onlineBus.booking.mapper;

import com.xypha.onlineBus.booking.dto.BookingResponse;
import com.xypha.onlineBus.booking.entity.Booking;
import org.apache.ibatis.annotations.*;


import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BookingMapper {

    @Insert("""
    INSERT INTO booking
    (booking_code, trip_id, user_id, total_amount, status,
     created_at, updated_at)
    VALUES
    (#{bookingCode}, #{tripId}, #{userId}, #{totalAmount}, #{status},
     #{createdAt}, #{updatedAt})
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

SELECT
    b.id,
    b.booking_code,
    b.trip_id,
    b.total_amount,
    b.status,
    b.created_at,
    b.updated_at,

    u.id AS user_id,
    u.username AS user_name,

    r.source AS route_source,
    r.destination AS route_destination,

    t.departure_date,
    t.arrival_date

FROM booking b
JOIN users u ON b.user_id = u.id
JOIN trip t ON b.trip_id = t.id
JOIN route r ON t.route_id = r.id

WHERE b.booking_code = #{bookingCode}


""")
    @Results({
            @Result(property = "id", column = "booking_id"),
            @Result(property = "bookingCode", column = "booking_code"),
            @Result(property = "tripId", column = "trip_id"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),

            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "routeSource", column = "route_source"),
            @Result(property = "routeDestination", column = "route_destination"),
            @Result(property = "departureDate", column = "departure_date"),
            @Result(property = "arrivalDate", column = "arrival_date"),

            @Result(
                    property = "seatNumbers",
                    column = "booking_id",
                    many = @Many(select = "getSeatNumbersByBookingId")
            )
    })

    Booking getByBookingCode(String bookingCode);


    @Select("""

SELECT
    b.id AS booking_id,
    b.booking_code,
    b.trip_id,
    b.total_amount,
    b.status,
    b.created_at,
    b.updated_at,

    u.id AS user_id,
    u.username AS user_name


FROM booking b
JOIN users u ON b.user_id = u.id

WHERE b.booking_code = #{bookingCode}


""")
    @Results({
            @Result(property = "id", column = "booking_id"),
            @Result(property = "bookingCode", column = "booking_code"),
            @Result(property = "tripId", column = "trip_id"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),

            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),


            @Result(
                    property = "seatNumbers",
                    column = "booking_id",
                    many = @Many(select = "getSeatNumbersByBookingId")
            )
    })
    BookingResponse searchByBookingCode(String bookingCode);


    @Select("SELECT trip_id FROM booking WHERE booking_code = #{bookingCode}")
    Long getTripIdByBookingCode(String bookingCode);



    @Update("""
            UPDATE booking SET status = #{status}, updated_at = NOW()
            WHERE booking_code = #{bookingCode}
            """)
    int updateStatus(@Param("bookingCode") String bookingCode, @Param("status") String status);


    @Select("""
            SELECT COUNT (*) FROM booking 
            WHERE booking_code = #{bookingCode}
            """)
    int countByBookingCode (String bookingCode);

    @Select("""
    SELECT s.id
    FROM seat s
    JOIN booking_seat bs ON s.id = bs.seat_id
    WHERE bs.booking_id = #{bookingId}
""")
    List<Long> getSeatIdsByBookingId (@Param("bookingId") Long bookingId);

    @Select("""
            SELECT s.seat_no
            FROM seat s
            JOIN booking_seat bs ON s.id = bs.seat_id
            WHERE bs.booking_id = #{bookingId}
            """)
    List<String> getSeatNumbersByBookingId (@Param("bookingId") Long bookingId);

    @Select("SELECT COUNT(*) FROM booking")
    int countAllBookings();

    @Select("SELECT COUNT (*) FROM booking where trip_id = #{tripId}")
    int countBookingsByTripId (Long id);


    void createBooking(Long id, Long seatId);

    @Select("""
        <script>
        SELECT b.id, b.booking_code, b.trip_id, b.user_id, b.total_amount, b.status, 
               b.created_at, b.updated_at, u.username AS user_name
        FROM booking b
        JOIN users u ON b.user_id = u.id
        <where>
            <if test='status != null and status != ""'>
                b.status = #{status}
            </if>
        </where>
        ORDER BY b.created_at DESC
        LIMIT #{limit} OFFSET #{offset}
        </script>
    """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "bookingCode", column = "booking_code"),
            @Result(property = "tripId", column = "trip_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "userName", column = "user_name")
    })
    List<Booking> getAllBookingsPaginated(@Param("status") String status,
                                          @Param("limit") int limit,
                                          @Param("offset") int offset);


    @Select("""
        <script>
        SELECT COUNT(*)
        FROM booking b
        <where>
            <if test='status != null and status != ""'>
                b.status = #{status}
            </if>
        </where>
        </script>
    """)
    int countBookingByStatus (@Param("status") String status);

    @Select("""
<script>
SELECT
    b.id AS booking_id,
    b.booking_code,
    b.total_amount,
    b.status,
    b.created_at,
    b.updated_at,

    u.id  AS user_id,
    u.username AS user_name,

    b.trip_id
FROM booking b
JOIN users u ON b.user_id = u.id
WHERE b.user_id = #{userId}
<if test="status != null and status != ''">
    AND b.status = #{status}
</if>
ORDER BY b.created_at DESC
LIMIT #{limit} OFFSET #{offset}
</script>
""")
    @Results({
            @Result(property = "bookingCode", column = "booking_code"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),

            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),

            @Result(property = "tripId", column = "trip_id"),

            @Result(
                    property = "seatNumbers",
                    column = "booking_id",
                    many = @Many(select = "getSeatNumbersByBookingId")
            )
    })
    List<BookingResponse> getUserBookingHistory(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("limit") int limit
    );



    @Select("""
<script>
SELECT COUNT(*)
FROM booking b
WHERE b.user_id = #{userId}
<if test="status != null and status != ''">
    AND b.status = #{status}
</if>
</script>
""")
    int countBookingHistory(
            @Param("userId") Long userId,
            @Param("status") String status
    );
    @Select("SELECT seat_no FROM booking_seat WHERE booking_id = #{bookingId}")
    List<String> getSeatNumberByBookingId (Long bookingId);


    @Update("""
            UPDATE booking 
            SET status = 'CANCELLED',
            updated_at = NOW()
            WHERE trip_id = #{tripId}
            AND status = 'BOOKED'
            """)
    int cancelAllBooingByTripId (@Param("tripId") Long tripId);


    @Delete("DELETE FROM booking WHERE trip_id = #{tripId} AND status IN ('PENDING', 'CANCELLED')")
    int deleteAllCancelledBookingsByTripId (@Param("tripId") Long tripId);


    @Select("""
            SELECT COUNT (*)
            FROM booking 
            WHERE trip_id = #{tripId}
            AND status IN ('PENDING', 'CONFIRMED')
            """)
    int countActiveBookingsByTripId (@Param("tripId") Long tripId);


    @Select("""
            SELECT b.id
            FROM booking b
            JOIN trip t ON t.id = b.trip_id
            WHERE b.status = 'PENDING'
            AND t.departure_date < #{now}
            """)
    List<Long> findExpiredPendingBookings (@Param("now") LocalDateTime now);

    @Delete("""
            DELETE FROM booking 
            WHERE status = 'CANCELLED'
            AND updated_at < #{cutoff}
            """)
    int deleteOldCancelledBookings(@Param("cutoff")LocalDateTime cutoff);

    @Select("""
            SELECT status 
            FROM booking WHERE booking_code = #{bookingCode}
            """)
    String getBookingStatus (@Param("bookingCode") String bookingCode);

    @Update("""
    <script>
    UPDATE booking
    SET status = 'CANCELLED',
        updated_at = #{now}
    WHERE id IN
    <foreach collection="bookingIds"
             item="item"
             open="("
             separator=","
             close=")">
        #{item}
    </foreach>
    </script>
""")
    int cancelBookingsByIds(
            @Param("bookingIds") List<Long> bookingIds,
            @Param("now") LocalDateTime now
    );



}


