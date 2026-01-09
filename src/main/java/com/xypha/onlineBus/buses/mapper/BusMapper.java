package com.xypha.onlineBus.buses.mapper;

import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import com.xypha.onlineBus.buses.services.Service;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BusMapper {

    // Insert Bus
    @Insert("INSERT INTO bus(bus_number, bus_type_id, total_seats, img_url, description, price_per_km, created_at, updated_at) " +
            "VALUES(#{busNumber}, #{busType.id}, #{totalSeats}, #{imgUrl}, #{description}, #{pricePerKm}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertBus(Bus bus);

    // Get Bus by ID (without services)
    @Select("""
    SELECT 
        b.id AS bus_id,
        b.bus_number,
        b.total_seats,
        b.img_url,
        b.description,
        b.price_per_km,
        b.created_at AS bus_created_at,
        b.updated_at AS bus_updated_at,
        bt.id AS bus_type_id,
        bt.name AS bus_type_name,
        bt.seat_per_row
    FROM bus b
    LEFT JOIN bus_type bt ON b.bus_type_id = bt.id
    WHERE b.id = #{id}
""")
    @Results({
            @Result(property = "id", column = "bus_id"),
            @Result(property = "busNumber", column = "bus_number"),
            @Result(property = "totalSeats", column = "total_seats"),
            @Result(property = "imgUrl", column = "img_url"),
            @Result(property = "description", column = "description"),
            @Result(property = "pricePerKm", column = "price_per_km"),
            @Result(property = "createdAt", column = "bus_created_at"),
            @Result(property = "updatedAt", column = "bus_updated_at"),
            @Result(property = "busType.id", column = "bus_type_id"),
            @Result(property = "busType.name", column = "bus_type_name"),
            @Result(property = "busType.seatPerRow", column = "seat_per_row")
    })
    Bus getBusById(Long id);


    // Pagination
    @Select("""
        SELECT 
            b.id AS bus_id,
            b.bus_number,
            b.total_seats,
            b.img_url,
            b.description,
            b.price_per_km,
            b.created_at AS bus_created_at,
            b.updated_at AS bus_updated_at,
            bt.id AS bus_type_id,
            bt.name AS bus_type_name,
            bt.seat_per_row
            
        FROM bus b
        LEFT JOIN bus_type bt ON b.bus_type_id = bt.id
        ORDER BY b.id DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
    @Results({
            @Result(property = "id", column = "bus_id"),
            @Result(property = "busNumber", column = "bus_number"),
            @Result(property = "totalSeats", column = "total_seats"),
            @Result(property = "imgUrl", column = "img_url"),
            @Result(property = "description", column = "description"),
            @Result(property = "pricePerKm", column = "price_per_km"),
            @Result(property = "createdAt", column = "bus_created_at"),
            @Result(property = "updatedAt", column = "bus_updated_at"),
            @Result(property = "busType.id", column = "bus_type_id"),
            @Result(property = "busType.name", column = "bus_type_name"),
            @Result(property = "busType.seatPerRow", column = "seat_per_row")
    })
    List<Bus> findPaginated(@Param("offset") int offset, @Param("limit") int limit);

    @Update("UPDATE bus SET bus_number=#{busNumber}, bus_type_id=#{busType.id}, total_seats=#{totalSeats}, " +
            "img_url=#{imgUrl}, description=#{description}, price_per_km=#{pricePerKm}, updated_at=NOW() WHERE id=#{id}")
    void updateBus(Bus bus);

    @Delete("DELETE FROM bus WHERE id = #{id}")
    void deleteBus(Long id);

    @Select("SELECT COUNT(*) FROM bus WHERE bus_number = #{busNumber}")
    int existsByBusNumber(String busNumber);

    @Select("SELECT COUNT(*) FROM bus")
    int countBuses();

    @Select("""
    SELECT s.id, s.name
    FROM service s
    JOIN bus_type_service bts ON bts.service_id = s.id
    WHERE bts.bus_type_id = #{busTypeId}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name")
    })
    List<Service> getServicesByBusTypeId(Long busTypeId);

    @Select("""
        SELECT 
            b.id AS bus_id,
            b.bus_number,
            b.total_seats,
            b.img_url,
            b.description,
            b.price_per_km,
            b.created_at AS bus_created_at,
            b.updated_at AS bus_updated_at,
            bt.id AS bus_type_id,
            bt.name AS bus_type_name,
            bt.seat_per_row
            
        FROM bus b
        LEFT JOIN bus_type bt ON b.bus_type_id = bt.id
        ORDER BY b.id DESC
	""")
    @Results({
            @Result(property = "id", column = "bus_id"),
            @Result(property = "busNumber", column = "bus_number"),
            @Result(property = "totalSeats", column = "total_seats"),
            @Result(property = "imgUrl", column = "img_url"),
            @Result(property = "description", column = "description"),
            @Result(property = "pricePerKm", column = "price_per_km"),
            @Result(property = "createdAt", column = "bus_created_at"),
            @Result(property = "updatedAt", column = "bus_updated_at"),
            @Result(property = "busType.id", column = "bus_type_id"),
            @Result(property = "busType.name", column = "bus_type_name"),
            @Result(property = "busType.seatPerRow", column = "seat_per_row")
    })
    List<Bus> getAllBus();


    @Select("""
            SELECT bus_number FROM bus WHERE id = #{id}
            """)
    String getBusNumberById(Long id);




}
