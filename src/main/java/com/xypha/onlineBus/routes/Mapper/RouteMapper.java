package com.xypha.onlineBus.routes.Mapper;

import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Entity.Route;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RouteMapper {

    @Insert("INSERT INTO route (source, destination, distance, created_at, updated_at)"+
    "VALUES(#{source}, #{destination}, #{distance}, NOW(), NOW() )")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRoute(Route route);

//    @Select("SELECT r.id, r.source, r.destination, r.distance, r.departure_time, r.arrival_time, " +
//            "b.id AS bus_id, b.bus_number, b.bus_type, b.total_seats, b.has_ac, b.has_wifi " +
//            "FROM route r LEFT JOIN bus b ON r.bus_id = b.id ORDER BY r.id DESC")
//    @Results({
//            @Result(property = "id", column = "id"),
//            @Result(property = "source", column = "source"),
//            @Result(property = "destination", column = "destination"),
//            @Result(property = "distance", column = "distance"),
//            @Result(property = "departureTime", column = "departure_time"),
//            @Result(property = "arrivalTime", column = "arrival_time"),
//            @Result(property = "bus", column = "bus_id",
//                    javaType = com.xypha.onlineBus.buses.Dto.BusResponse.class,
//                    one = @One(select = "com.xypha.onlineBus.buses.Mapper.BusMapper.getBusResponseById"))
//
//    })
//    List<RouteResponse> getAllRoute(@Param("offset") int offset,
//                            @Param("limit") int limit);

    @Select("SELECT r.id, r.source, r.destination, r.distance, r.created_at, r.updated_at " +
            "FROM route r WHERE r.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "source", column = "source"),
            @Result(property = "destination", column = "destination"),
            @Result(property = "distance", column = "distance"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),

    })
    Route getRouteById(Long id);

    @Update("UPDATE route SET source=#{source}, destination=#{destination}, distance =#{distance} ,updated_at= NOW() WHERE id=#{id}")
    void updateRoute(Route route);

    @Delete("DELETE FROM route WHERE id = #{id}")
    void deleteRoute(Long id);

    @Select("SELECT COUNT(*) FROM route WHERE source=#{source} AND destination=#{destination}")
    int countDuplicateRoute(Route route);



    //Search Method
    @Select("<script>"
            + "SELECT COUNT(*) FROM route WHERE 1=1 "
            + "<if test='source != null and source != \"\"'> AND source LIKE CONCAT('%', #{source}, '%') </if>"
            + "<if test='destination != null and destination != \"\"'> AND destination LIKE CONCAT('%', #{destination}, '%') </if>"
            + "<if test='departureDate != null'> AND DATE(departure_time) = #{departureDate} </if>"
            + "</script>")
    int countSearchRoutes(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") LocalDate departureDate
    );


    @Select("<script>"
            + "SELECT "
            + "r.id AS route_id, "
            + "r.source, "
            + "r.destination, "
            + "r.distance, "
            + "r.departure_time, "
            + "r.arrival_time, "

            + "b.id AS bus_id, "
            + "b.bus_number, "
            + "b.bus_type_id, "
            + "b.total_seats, "
            + "b.img_url, "
            + "b.description, "
            + "b.price_per_km, "

            + "d.id AS driver_id, "
            + "d.name AS driver_name, "
            + "d.employee_id AS driver_employee_id, "

            + "a.id AS assistant_id, "
            + "a.name AS assistant_name, "
            + "a.employee_id AS assistant_employee_id, "

            + "FROM route r "
            + "LEFT JOIN bus b ON r.bus_id = b.id "
            + "LEFT JOIN driver d ON b.driver_id = d.id "
            + "LEFT JOIN assistant a ON b.assistant_id = a.id "
            + "WHERE 1=1 "
            + "<if test='source != null and source != \"\"'> AND r.source LIKE CONCAT('%', #{source}, '%') </if>"
            + "<if test='destination != null and destination != \"\"'> AND r.destination LIKE CONCAT('%', #{destination}, '%') </if>"
            + "<if test='departureDate != null'> AND DATE(r.departure_time) = #{departureDate} </if>"
            + "ORDER BY r.departure_time ASC "
            + "LIMIT #{limit} OFFSET #{offset} "
            + "</script>")
    @Results({
            @Result(property = "id", column = "route_id"),
            @Result(property = "source", column = "source"),
            @Result(property = "destination", column = "destination"),
            @Result(property = "distance", column = "distance"),
            @Result(property = "departureTime", column = "departure_time"),
            @Result(property = "arrivalTime", column = "arrival_time"),

            @Result(property = "bus.id", column = "bus_id"),
            @Result(property = "bus.busNumber", column = "bus_number"),
            @Result(property = "bus.busType", column = "bus_type"),
            @Result(property = "bus.totalSeats", column = "total_seats"),
            @Result(property = "bus.imgUrl", column = "img_url"),
            @Result(property = "bus.description", column = "description"),
            @Result(property = "bus.pricePerKm", column = "price_per_km"),


            @Result(property = "bus.driver.id", column = "driver_id"),
            @Result(property = "bus.driver.name", column = "driver_name"),
            @Result(property = "bus.driver.employeeId", column = "driver_employee_id"),

            @Result(property = "bus.assistant.id", column = "assistant_id"),
            @Result(property = "bus.assistant.name", column = "assistant_name"),
            @Result(property = "bus.assistant.employeeId", column = "assistant_employee_id")
    })
    List<RouteResponse> searchRoutes(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") LocalDate departureDate,
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

    @Select("SELECT COUNT (*) FROM route")
    int countRoutes();


    @Select("""
        SELECT r.id AS route_id,
               r.source,
               r.destination,
               r.distance,
               r.created_at,
               r.updated_at
        FROM route r
        ORDER BY r.id DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    @Results({
            @Result(property = "id", column = "route_id"),
            @Result(property = "source", column = "source"),
            @Result(property = "destination", column = "destination"),
            @Result(property = "distance", column = "distance"),
            @Result(property = "arrivalTime", column = "arrival_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Route> getAllPaginated(
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
    SELECT COUNT(*) FROM route
    WHERE REPLACE(UPPER(source), ' ', '') = REPLACE(UPPER(#{source}), ' ', '')
      AND REPLACE(UPPER(destination), ' ', '') = REPLACE(UPPER(#{destination}), ' ', '')
""")
    int countDuplicateNormalizeRoute(
            @Param("source") String source,
            @Param("destination") String destination
    );
    @Select("""
            SELECT COUNT(*) FROM route
            WHERE REPLACE(UPPER(source), ' ', '') = REPLACE(UPPER(#{source}), ' ', '')
            """)
    int countByNormalizedSource(@Param("source") String source);

    @Select("""
            SELECT COUNT(*) FROM route
            WHERE REPLACE(UPPER(destination), ' ', '') = REPLACE(UPPER(#{destination}), ' ', '')
            """)
    int countByNormalizedDestination(@Param("destination") String destination);

    @Select("""
    SELECT COUNT(*) FROM route
    WHERE id != #{id}
      AND (REPLACE(UPPER(source), ' ', '') = REPLACE(UPPER(#{source}), ' ', '')
           OR REPLACE(UPPER(destination), ' ', '') = REPLACE(UPPER(#{destination}), ' ', ''))
""")
    int countByNormalizedSourceOrDestinationExcludingId(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("id") Long id
    );

}
