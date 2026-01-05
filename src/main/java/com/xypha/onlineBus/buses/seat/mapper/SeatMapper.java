package com.xypha.onlineBus.buses.seat.mapper;

import com.xypha.onlineBus.buses.seat.entity.Seat;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SeatMapper {

    @Insert("""
        INSERT INTO seat (trip_id, seat_no, status, created_at, updated_at)
        VALUES (#{tripId}, #{seatNo}, #{status}, NOW(), NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createSeat(Seat seat);




    @Select("""
        SELECT COUNT(*)
        FROM seat
        WHERE trip_id = #{tripId}
    """)
    int countSeatsByTripId(Long tripId);


    @Select("""
        SELECT COUNT(*) FROM seat
        WHERE trip_id = #{tripId}
    """)
    int countByTripId(Long tripId);


    @Select("SELECT COUNT(*) FROM seat WHERE trip_id = #{tripId} AND status = 0")
    int countAvailableSeatsByTripId(Long tripId);

    @Select("SELECT COUNT(*) FROM seat WHERE trip_id = #{tripId} AND status = 1")
    int countBookedSeatsByTripId(Long tripId);

    @Select("SELECT COUNT(*) FROM seat")
    int countSeats();

    @Select("""
        SELECT * FROM seat
        ORDER BY id
        LIMIT #{limit} OFFSET #{offset}
    """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "seatNo", column = "seat_no"),
            @Result(property = "tripId", column = "trip_id"),
            @Result(property = "status", column = "status")
    })
    List<Seat> getAllPaginated(@Param("offset") int offset, @Param("limit") int limit);



    @Select("""
        SELECT * FROM seat
        WHERE id = #{id}
        ORDER BY seat_no
    """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "seatNo", column = "seat_no"),
            @Result(property = "tripId", column = "trip_id"),
            @Result(property = "status", column = "status")
    })
    Seat getSeatById(Long id);




    @Select("SELECT * FROM seat WHERE trip_id = #{tripId} ORDER BY seat_no")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "seatNo", column = "seat_no"),
            @Result(property = "tripId", column = "trip_id"),
            @Result(property = "status", column = "status")
    })
    List<Seat> findSeatsByTripId(Long tripId);




    ////final part
    @Select("""
            SELECT * FROM seat
            WHERE trip_id = #{tripId} AND
            seat_no = #{seatNo}
            """)
    Seat getSeatByTripAndNo(@Param("tripId") Long tripId,
                            @Param("seatNo") String seatNo);

    @Select("UPDATE seat SET status = #{status} WHERE id = #{id}")
    void updateSeatStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("UPDATE seat SET status = #{status} WHERE id = #{id}")
    void updateSeat (Seat seat);

    ////Lock the seat for concoury state
    @Select("""
    SELECT *
    FROM seat
    WHERE trip_id = #{tripId} AND seat_no = #{seatNo}
    FOR UPDATE
""")
    Seat lockSeatForUpdate(
            @Param("tripId") Long tripId,
            @Param("seatNo") String seatNo
    );

    @Update("""
            UPDATE seat SET status = 1,
            updated_at = NOW()
            WHERE trip_id = #{tripId}
            """)
    int releaseAllSeatsByTrip (@Param("tripId") Long tripId);

    @Update("""
    <script>
    UPDATE seat s
    SET status = 0
    WHERE s.id IN (
        SELECT bs.seat_id
        FROM booking_seat bs
        WHERE bs.booking_id IN
        <foreach collection="bookingIds"
                 item="item"
                 open="("
                 separator=","
                 close=")">
            #{item}
        </foreach>
    )
    </script>
""")
    int releaseSeatsByBookingIds(@Param("bookingIds") List<Long> bookingIds);

    @Delete("""
            DELETE FROM seat WHERE trip_id = #{tripId}
            """)
    int deleteSeatsByTripId(@Param("tripId") Long tripId);

}
