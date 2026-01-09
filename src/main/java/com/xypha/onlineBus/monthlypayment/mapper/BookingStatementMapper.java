package com.xypha.onlineBus.monthlypayment.mapper;

import com.xypha.onlineBus.monthlypayment.dto.DailyTripResponse;
import com.xypha.onlineBus.monthlypayment.dto.DailyTripStatementRow;
import com.xypha.onlineBus.monthlypayment.entity.DailyTripReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BookingStatementMapper {
    @Select("""
        SELECT
            t.id AS tripId,
            r.id AS routeId,
            t.bus_id AS busId,
            COUNT(b.id) AS seatsSold,
            bus.total_seats AS totalSeats,
            COALESCE(SUM(b.total_amount), 0) AS tripRevenue,
            ROUND(
                (COUNT(b.id)::decimal / bus.total_seats) * 100, 2
            ) AS loadFactor
        FROM trip t
        JOIN route r ON r.id = t.route_id
        JOIN bus ON bus.id = t.bus_id
        LEFT JOIN booking b
            ON b.trip_id = t.id
            AND b.status = 'CONFIRMED'
        WHERE DATE(t.departure_date) = #{date}
        GROUP BY t.id, r.id, t.bus_id, bus.total_seats
        ORDER BY t.departure_date
    """)
    List<DailyTripStatementRow> getTripStatementsByDate(LocalDate date);



    @Select("""
            SELECT\s
                COUNT(DISTINCT t.id) AS totalTrips,
                COUNT(b.id) AS totalSeatsSold,
                SUM(bus.total_seats) AS totalSeats,
                COALESCE(SUM(b.total_amount), 0) AS totalRevenue,
                ROUND(
                    CASE WHEN SUM(bus.total_seats) > 0 THEN (COUNT(b.id)::decimal / SUM(bus.total_seats)) * 100
                    ELSE 0 END,
                    2
                ) AS averageLoadFactor
            FROM trip t
            JOIN bus ON bus.id = t.bus_id
            LEFT JOIN booking b
                ON b.trip_id = t.id
                AND b.status = 'CONFIRMED'
            WHERE DATE(t.departure_date) = #{date};
                      
            """)
    DailyTripResponse getDailySummary (LocalDate date);


    @Select("""
            SELECT
                t.id AS tripId,
                t.departure_date AS departureDate,
                t.arrival_date AS arrivalDate,
                bus.bus_number AS busNumber,
                r.id AS routeId,
                r.source_city_id AS sourceCityId,
                r.destination_city_id AS destinationCityId,
                d.name AS driverName,
                a.name AS assistantName,
                COUNT(DISTINCT bk.id) AS totalSeatsSold,
                SUM(bk.total_amount) AS totalRevenue,
                bus.total_seats AS totalSeats
            FROM trip t
            JOIN bus bus ON bus.id = t.bus_id
            JOIN route r ON r.id = t.route_id
            LEFT JOIN booking bk ON bk.trip_id = t.id AND bk.status = 'CONFIRMED'
            LEFT JOIN driver d ON d.id = t.driver_id
            LEFT JOIN assistant a ON a.id = t.assistant_id
            WHERE t.departure_date >= #{fromDate} AND t.departure_date <= #{toDate}
            GROUP BY t.id, bus.bus_number, r.id, r.source_city_id, r.destination_city_id, d.name, a.name, bus.total_seats
            ORDER BY t.departure_date ASC
                        
            """)
    List<DailyTripReport> getTripsBetweenDates (
            @Param("fromDate")LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
            );




}
