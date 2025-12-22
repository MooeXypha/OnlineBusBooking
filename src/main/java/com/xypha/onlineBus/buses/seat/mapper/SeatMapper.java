package com.xypha.onlineBus.buses.seat.mapper;

import com.xypha.onlineBus.buses.seat.entity.Seat;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SeatMapper {

    @Insert("""
        INSERT INTO seat (trip_id, seat_no, status, create_at, updated_at)
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



}
