package com.xypha.onlineBus.buses.busTypeLayout.mapper;

import com.xypha.onlineBus.buses.busTypeLayout.entity.Seat;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface SeatMapper {

    @Insert("INSERT INTO seat (trip_id,seat_number,booked) VALUES (#{tripId}, #{seatNumber}, false) ")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createSeat(Seat seat);

    @Select("SELECT * FROM seat WHERE trip_id = #{tripId} ORDER BY seat_number")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "tripId", column = "trip_id"),
            @Result(property = "seatNumber", column = "seat_number"),
            @Result(property = "booked", column = "booked"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Seat> getSeatByTrip (@Param("tripId") Long tripId);

    @Update("UPDATE seat SET booked = true, updated_at = NOW() WHERE trip_id = #{tripId} AND seat_number = #{seatNumber} ")
    void bookSeat (@Param("tripId") Long tripId,
                   @Param("seatNumber") String seatNumber
    );;

    @Update("UPDATE seat SET booked = false, updated_at = NOW() WHERE trip_id = #{tripID} AND seat_number = #{seatNumber}")
    void avaliableSeat (
            @Param("tripId") Long tripId,
            @Param("seatNumber") String seatNumber
    );





}
