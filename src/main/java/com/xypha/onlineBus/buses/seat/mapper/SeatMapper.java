package com.xypha.onlineBus.buses.seat.mapper;

import com.xypha.onlineBus.buses.seat.entity.Seat;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SeatMapper {

    @Insert("""
        INSERT INTO seat (trip_id, seat_no, status)
        VALUES (#{tripId}, #{seatNo}, #{status})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createSeat(Seat seat);

    @Select("""
        SELECT * FROM seat
        WHERE trip_id = #{tripId}
        ORDER BY seat_no
    """)
    List<Seat> getSeatByTrip(Long tripId);


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

    @Update("""
        UPDATE seat
        SET status = #{status}
        WHERE trip_id = #{tripId}
          AND seat_no = #{seatNo}
          AND status = 0
    """)
    int updateSeatStatus(
            @Param("tripId") Long tripId,
            @Param("seatNo") String seatNo,
            @Param("status") int status
    );

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
        ORDER BY id
    """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "seatNo", column = "seat_no"),
            @Result(property = "tripId", column = "trip_id"),
            @Result(property = "status", column = "status")
    })
    Seat getSeatById(Long id);


    @Select("DELETE FROM seat WHERE id = #{id}")
    void deleteSeat(Long id);

    @Select("SELECT * FROM seat WHERE trip_id = #{tripId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "seatNo", column = "seat_no"),
            @Result(property = "tripId", column = "trip_id"),
            @Result(property = "status", column = "status")
    })
    List<Seat> findSeatsByTripId(Long tripId);

    void updateSeat(Seat seat);
}
