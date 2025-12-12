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
            + "SELECT "
            + "r.id AS route_id, "
            + "r.source, "
            + "r.destination, "
            + "r.distance, "
            + "r.created_at, "
            + "r.updated_at "
            + "FROM route r "
            + "WHERE 1=1 "
            + "<if test='source != null and source != \"\"'> "
            + "AND UPPER(r.source) LIKE CONCAT('%', UPPER(#{source}), '%') "
            + "</if>"
            + "<if test='destination != null and destination != \"\"'> "
            + "AND UPPER(r.destination) LIKE CONCAT('%', UPPER(#{destination}), '%') "
            + "</if>"
            + "ORDER BY r.id ASC "
            + "LIMIT #{limit} OFFSET #{offset} "
            + "</script>")
    @Results({
            @Result(property = "id", column = "route_id"),
            @Result(property = "source", column = "source"),
            @Result(property = "destination", column = "destination"),
            @Result(property = "distance", column = "distance"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<RouteResponse> searchRoutes(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Select("<script>"
            + "SELECT COUNT(*) "
            + "FROM route r "
            + "WHERE 1=1 "
            + "<if test='source != null and source != \"\"'> "
            + "AND UPPER(r.source) LIKE CONCAT('%', UPPER(#{source}), '%') "
            + "</if>"
            + "<if test='destination != null and destination != \"\"'> "
            + "AND UPPER(r.destination) LIKE CONCAT('%', UPPER(#{destination}), '%') "
            + "</if>"
            + "</script>")
    int countSearchRoutes(
            @Param("source") String source,
            @Param("destination") String destination
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
    int countDuplicateNormalizeCreateRoute(
            @Param("source") String source,
            @Param("destination") String destination
    );

    @Select("""
    SELECT COUNT(*) FROM route
    WHERE id != #{id}
    WHERE REPLACE(UPPER(source), ' ', '') = REPLACE(UPPER(#{source}), ' ', '')
      AND REPLACE(UPPER(destination), ' ', '') = REPLACE(UPPER(#{destination}), ' ', '')
""")
    int countDuplicateNormalizeUpdateRoute(
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

    @Select("""
    SELECT COUNT(*) FROM route
    WHERE id != #{id}
      AND REPLACE(UPPER(source), ' ', '') = #{normalizedSource}
      AND REPLACE(UPPER(destination), ' ', '') = #{normalizedDestination}
""")
    int countDuplicateNormalizeRouteExcludingId(
            @Param("id") Long id,
            @Param("normalizedSource") String normalizedSource,
            @Param("normalizedDestination") String normalizedDestination
    );


}
