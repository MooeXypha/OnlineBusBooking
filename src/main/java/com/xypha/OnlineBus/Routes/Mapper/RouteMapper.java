package com.xypha.OnlineBus.Routes.Mapper;

import com.xypha.OnlineBus.Routes.Entity.Route;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RouteMapper {

    @Insert("INSERT INTO route (source,destination,price,departure_time,arrival_time)"+
    "VALUES(#{source}, #{destinatio}, #{price}, #{departure_time}, #{arrival_time})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRoute(Route route);

    @Select("SELECT * FROM route")
    List<Route> getAllRoute();

    @Select("SELECT * FROM route WHERE id= #{id")
    Route getRouteById(Long id);

    @Update("UPDATE route SET source= #{source}, destination=#{destination}, departure_time=#{departure_time}, arrival_time=#{arrival_time) WHERE id= #{id} ")
    void updateRoute(Route route);

    @Delete("DELETE FROM route WHERE id = #{id}")
    void deleteRoute(Long id);

    @Select("SELECT COUNT(*) FROM route WHERE source=#{source} AND destination=#{destination} " +
            "AND departure_time=#{departureTime} AND arrival_time=#{arrivalTime}")
    int countDuplicateRoute(Route route);



    //Search Method
    @Select("<script>"
            + "SELECT * FROM route WHERE 1=1 "
            + "<if test='source != null and source != \"\"'> AND source LIKE CONCAT('%', #{source}, '%') </if>"
            + "<if test='destination != null and destination != \"\"'> AND destination LIKE CONCAT('%', #{destination}, '%') </if>"
            + " LIMIT #{limit} OFFSET #{offset} "
            + "</script>")
    int countSearchRoutes(
            @Param("source") String source,
            @Param("destination") String destination
    );

    List<Route> searchRoutes(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("limit") int limit,
            @Param("offset") int offset
    );



    //A driver or assistant cannot be assigned to more than one bus on the same day.
    //
    //A bus cannot be assigned to more than one route on the same day.
    //
    //A driver or assistant cannot be assigned to more than one route on the same day.


    @Select("SELECT COUNT (*) FROM route WHERE bus_id = #{busId} AND DATE(departure_time) = #{date} ")
    int countBusAssignmentsForDate(
            @Param("busId") Long busId,
            @Param("date") LocalDate date);

    @Select("SELECT COUNT (*) FROM route WHERE driver_id = #{driverId} AND DATE(departure_time) = #{date} ")
    int countDriverAssignmentsForDate(
            @Param("driverId") Long driverId,
            @Param("date") LocalDate date);

    @Select("SELECT COUNT (*) FROM route WHERE assistant_id = #{assistantId} AND DATE(departure_time) = #{date} ")
    int countAssistantAssignmentsForDate(
            @Param("assistantId") Long assistantId,
            @Param("date") LocalDate date);


}
