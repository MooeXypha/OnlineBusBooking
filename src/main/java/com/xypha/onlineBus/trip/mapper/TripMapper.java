package com.xypha.onlineBus.trip.mapper;

import com.xypha.onlineBus.trip.entity.Trip;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TripMapper {

    ///////////////Create new trip
    @Insert("INSERT INTO trip (route_id, bus_id, departure_date, arrival_date, fare, created_at, updated_at)" +
            "VALUES(#{routeId}, #{busId}, #{departureDate}, #{arrivalDate}, #{fare}, NOW(), NOW() )")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createTrip(Trip trip);

    ////////////////Get by Id
    @Select("SELECT t.id, t.route_id, t.bus_id, t.departure_date, t.arrival_date, t.fare, t.created_at, t.updated_at " +
            "FROM trip t WHERE t.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "routeId", column = "route_id"),
            @Result(property = "busId", column = "bus_id"),
            @Result(property = "departureDate", column = "departure_date"),
            @Result(property = "arrivalDate", column = "arrival_date"),
            @Result(property = "fare", column = "fare"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Trip getTripById(Long id);

    @Update("UPDATE trip SET route_id=#{routeId}, bus_id=#{busId}, departure_date=#{departureDate}, arrival_date=#{arrivalDate}, fare=#{fare}, updated_at= NOW() WHERE id=#{id}")
    void updateTrip(Trip trip);

    @Delete("DELETE FROM trip WHERE id = #{id}")
    void deleteTrip(Long id);

    @Select("SELECT COUNT(*) FROM trip WHERE route_id=#{routeId} AND bus_id=#{busId} AND departure_date=#{departureDate}")
    int countDuplicateTrip(Trip trip);

    @Select("SELECT COUNT(*) FROM trip")
    int countTrip();

    @Select("""
            SELECT t.id, t.route_id, t.bus_id, t.departure_date, t.arrival_date, t.fare, t.created_at, t.updated_at
            FROM trip t 
            ORDER BY t.id DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "routeId", column = "route_id"),
            @Result(property = "busId", column = "bus_id"),
            @Result(property = "departureDate", column = "departure_date"),
            @Result(property = "arrivalDate", column = "arrival_date"),
            @Result(property = "fare", column = "fare"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Trip> getAllTripsPaginated(
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    //Search trip by departure date
    @Select("""
            SELECT t.id, t.route_id, t.bus_id, t.departure_date, t.arrival_date, t.fare, t.created_at, t.updated_at
            FROM trip t
            WHERE DATE(t.departure_date) = #{departureDate}
            ORDER BY t.departure_date DESC
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "routeId", column = "route_id"),
            @Result(property = "busId", column = "bus_id"),
            @Result(property = "departureDate", column = "departure_date"),
            @Result(property = "arrivalDate", column = "arrival_date"),
            @Result(property = "fare", column = "fare"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Trip> searchTripsByDepartureDate(@Param("departureDate") LocalDate departureDate);


    @Select("SELECT COUNT (*) FROM trip WHERE DATE(departure_date) = #{departureDate}")
    int countTripsByDepartureDate(@Param("departureDate") LocalDate departureDate);

}