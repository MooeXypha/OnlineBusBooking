package com.xypha.onlineBus.trip.mapper;

import com.xypha.onlineBus.buses.services.Service;
import com.xypha.onlineBus.trip.dto.TripResponse;
import com.xypha.onlineBus.trip.entity.Trip;
import org.apache.ibatis.annotations.*;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
            @Param("departureDate") OffsetDateTime departureDate,
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
            @Param("date") OffsetDateTime date,
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
            @Param("date") OffsetDateTime date,
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
            @Param("departureDate") OffsetDateTime departureDate,
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
    List<Long> findExpiredTripIds(@Param("now") OffsetDateTime now);

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





}