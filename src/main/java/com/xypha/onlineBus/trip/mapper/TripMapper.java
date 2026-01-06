package com.xypha.onlineBus.trip.mapper;

import com.xypha.onlineBus.buses.services.Service;
import com.xypha.onlineBus.routes.Dto.RouteWithCity;
import com.xypha.onlineBus.trip.dto.TripResponse;
import com.xypha.onlineBus.trip.entity.Trip;
import org.apache.ibatis.annotations.*;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TripMapper {

    // Create a new trip
    @Insert("INSERT INTO trip (route_id, bus_id, departure_date, arrival_date, duration, fare, driver_id, assistant_id, created_at, updated_at)" +
            " VALUES (#{routeId}, #{busId}, #{departureDate}, #{arrivalDate}, #{duration}, #{fare}, #{driverId}, #{assistantId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createTrip(Trip trip);

    // Get trip by ID
    @Select("SELECT t.id, t.route_id, t.bus_id, t.driver_id, t.assistant_id, t.departure_date, t.arrival_date, t.duration ,t.fare, t.created_at, t.updated_at " +
            "FROM trip t WHERE t.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "routeId", column = "route_id"),
            @Result(property = "busId", column = "bus_id"),
            @Result(property = "driverId", column = "driver_id"),
            @Result(property = "assistantId", column = "assistant_id"),
            @Result(property = "departureDate", column = "departure_date"),
            @Result(property = "arrivalDate", column = "arrival_date"),
            @Result(property = "fare", column = "fare"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Trip getTripById(Long id);

    @Select("""
    SELECT 
        t.id AS trip_id,
        t.route_id,
        t.bus_id,
        t.driver_id,
        t.assistant_id,
        t.departure_date,
        t.arrival_date,
        t.duration,
        t.fare,
        t.created_at,
        t.updated_at,
        r.source_city_id,
        r.destination_city_id,
        r.distance AS route_distance,
        c1.name AS source_name,
        c2.name AS destination_name
    FROM trip t
    JOIN route r ON t.route_id = r.id
    LEFT JOIN city c1 ON r.source_city_id = c1.id
    LEFT JOIN city c2 ON r.destination_city_id = c2.id
    WHERE t.id = #{id}
""")
    @Results({
            @Result(property="id", column="trip_id"),
            @Result(property="busId", column="bus_id"),
            @Result(property="routeId", column="route_id"),
            @Result(property="driverId", column="driver_id"),
            @Result(property="assistantId", column="assistant_id"),
            @Result(property="departureDate", column="departure_date"),
            @Result(property="arrivalDate", column="arrival_date"),
            @Result(property="fare", column="fare"),
            @Result(property="duration", column="duration"),
            @Result(property="createdAt", column="created_at"),
            @Result(property="updatedAt", column="updated_at"),
            // Optional: can map route fields to nested object if needed
    })
    Trip getTripWithRouteAndCity(Long id);

    // Update trip
    @Update("UPDATE trip SET route_id=#{routeId}, bus_id=#{busId}, departure_date=#{departureDate}, arrival_date=#{arrivalDate}, duration=#{duration}," +
            "fare=#{fare}, driver_id=#{driverId}, assistant_id=#{assistantId}, updated_at=NOW() WHERE id=#{id}")
    void updateTrip(Trip trip);

    @Update("""
            UPDATE trip SET status = #{status} , updated_at = NOW() WHERE id = #{id}
            """)
    void updateTripStatus(Long id, String status);

    // Delete trip
    @Delete("DELETE FROM trip WHERE id=#{id}")
    int deleteTrip(Long id);

    // Count duplicate trip
    @Select("""
    SELECT COUNT(*) 
    FROM trip 
    WHERE route_id = #{routeId} 
      AND bus_id = #{busId} 
      AND DATE(departure_date) = DATE(#{departureDate})
      AND (#{excludeId,jdbcType=BIGINT} IS NULL 
           OR id != #{excludeId,jdbcType=BIGINT})
""")
    int countDuplicateTrip(
            @Param("routeId") Long routeId,
            @Param("busId") Long busId,
            @Param("departureDate") LocalDateTime departureDate,
            @Param("excludeId") Long excludeId
    );

    // Count all trips
    @Select("SELECT COUNT(*) FROM trip")
    int countTrip();

    // Paginated trips
    @Select("SELECT t.id, t.route_id, t.bus_id, t.driver_id, t.assistant_id, t.departure_date, t.arrival_date, t.fare, t.duration, t.created_at, t.updated_at " +
            "FROM trip t ORDER BY t.id DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "routeId", column = "route_id"),
            @Result(property = "busId", column = "bus_id"),
            @Result(property = "driverId", column = "driver_id"),
            @Result(property = "assistantId", column = "assistant_id"),
            @Result(property = "departureDate", column = "departure_date"),
            @Result(property = "arrivalDate", column = "arrival_date"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "fare", column = "fare"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Trip> getAllTripsPaginated(@Param("offset") int offset, @Param("limit") int limit);


    // Check assignments to avoid conflicts
    @Select("""
                SELECT COUNT(*)
                FROM trip
                WHERE bus_id = #{busId}
                  AND DATE(departure_date) = DATE(#{date})
                  AND (#{excludeId,jdbcType=BIGINT} IS NULL OR id != #{excludeId,jdbcType=BIGINT})
            """)
    int countBusAssignments(
            @Param("busId") Long busId,
            @Param("date") LocalDateTime date,
            @Param("excludeId") Long excludeId
    );

    @Select("""
                SELECT COUNT(*)
                FROM trip
                WHERE assistant_id = #{assistantId}
                  AND DATE(departure_date) = DATE(#{date})
                  AND (#{excludeId,jdbcType=BIGINT} IS NULL OR id != #{excludeId,jdbcType=BIGINT})
            """)
    int countAssistantAssignments(
            @Param("assistantId") Long assistantId,
            @Param("date") LocalDateTime date,
            @Param("excludeId") Long excludeId
    );

    @Select("""
            SELECT COUNT(*)
            FROM trip
            WHERE driver_id = #{driverId}
              AND DATE(departure_date) = DATE(#{departureDate})
              AND (#{excludeId,jdbcType=BIGINT} IS NULL OR id != #{excludeId,jdbcType=BIGINT})
            """)
    int countDriverAssignments(
            @Param("driverId") Long driverId,
            @Param("departureDate") LocalDateTime departureDate,
            @Param("excludeId") Long excludeId
    );

    @Select("""
                SELECT COUNT(*)
                FROM trip t
                JOIN bus b ON t.bus_id = b.id
                WHERE t.route_id = #{routeId}
                  AND b.bus_type_id = #{busTypeId}
                  AND DATE(t.departure_date) = #{date}
                  AND (#{excludeId,jdbcType=BIGINT} IS NULL OR t.id != #{excludeId,jdbcType=BIGINT})
            """)
    int countSameBusTypeOnRoute(
            @Param("routeId") Long routeId,
            @Param("busTypeId") Long busTypeId,
            @Param("date") LocalDate date,
            @Param("excludeId") Long excludeId
    );

    /////Search trip with source/destination/departureDate
    @Select("""
        SELECT
            t.id,
            t.route_id,
            t.bus_id,
            t.driver_id,
            t.assistant_id,
            t.departure_date,
            t.arrival_date,
            t.duration,
            t.fare,
            t.created_at,
            t.updated_at
        FROM trip t
        JOIN route r ON r.id = t.route_id
        WHERE
            (#{source} IS NULL
                OR REPLACE(UPPER(r.source), ' ', '') LIKE CONCAT('%', REPLACE(UPPER(#{source}), ' ', ''), '%'))
        AND
            (#{destination} IS NULL
                OR REPLACE(UPPER(r.destination), ' ', '') LIKE CONCAT('%', REPLACE(UPPER(#{destination}), ' ', ''), '%'))
        AND
            (#{departureDate,jdbcType=DATE} IS NULL
                OR DATE(t.departure_date) = #{departureDate,jdbcType=DATE})
        """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "routeId", column = "route_id"),
            @Result(property = "busId", column = "bus_id"),
            @Result(property = "driverId", column = "driver_id"),
            @Result(property = "assistantId", column = "assistant_id"),
            @Result(property = "departureDate", column = "departure_date"),
            @Result(property = "arrivalDate", column = "arrival_date"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "fare", column = "fare"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Trip> searchTrips(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") LocalDate departureDate
    );

    @Select("""
    SELECT id,
           bus_id,
           route_id,
           driver_id,
           assistant_id,
           departure_date,
           arrival_date,
           duration,
           fare,
           created_at,
           updated_at
    FROM trip
    WHERE id = #{tripId}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "busId", column = "bus_id"),
            @Result(property = "routeId", column = "route_id"),
            @Result(property = "driverId", column = "driver_id"),
            @Result(property = "assistantId", column = "assistant_id"),
            @Result(property = "departureDate", column = "departure_date"),
            @Result(property = "arrivalDate", column = "arrival_date"),
            @Result(property = "duration", column = "duration"),
            @Result(property = "fare", column = "fare"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Trip getTripFareById(Long id);

    @Select("""
            SELECT id
            FROM trip 
            WHERE arrival_date < NOW ()
            """)
    List<Long> findExpiredTripIds(@Param("now") LocalDateTime now);

    @Select("""
            SELECT COUNT (*) FROM trip 
            WHERE route_id = #{routeId}
            AND departure_date > NOW()
            """)
    int countActiveTripsByRouteId (@Param("routeId") Long routeId);

    @Select("""
    SELECT id
    FROM trip
    WHERE route_id = #{routeId}
      AND arrival_date < NOW()
""")
    List<Long> findExpiredTripsByRouteId(@Param("routeId") Long routeId);

    @Select("""
            SELECT id 
            FROM trip
            WHERE departure_date < #{now}
            """)
    List<Long> findDepartedTripIds (@Param("now")LocalDateTime now);

    @Select("""
            SELECT COUNT (*)
            FROM trip
            WHERE route_id = #{routeId}
            """)
    int countTripsByRouteId (@Param("routeId")Long routeId);

    @Select("""
    SELECT
        t.id,
        t.route_id,
        t.bus_id,
        t.driver_id,
        t.assistant_id,
        t.departure_date,
        t.arrival_date,
        t.duration,
        t.fare,
        t.created_at,
        t.updated_at
    FROM trip t
    WHERE t.id = #{id}
    """)
    TripResponse getTripResponseById(Long id);

    @Select("""
        SELECT 
            r.id AS id,
            r.distance AS distance,
            r.created_at AS createdAt,
            r.updated_at AS updatedAt,
            cs.name AS sourceName,
            cd.name AS destinationName
        FROM trip t
        INNER JOIN route r ON t.route_id = r.id
        INNER JOIN city cs ON r.source_city_id = cs.id
        INNER JOIN city cd ON r.destination_city_id = cd.id
        WHERE t.id = #{tripId}
    """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "distance", column = "distance"),
            @Result(property = "createdAt", column = "createdAt"),
            @Result(property = "updatedAt", column = "updatedAt"),
            @Result(property = "sourceName", column = "sourceName"),
            @Result(property = "destinationName", column = "destinationName")
    })
    RouteWithCity getRouteWithCityByTripId(Long tripId);



}