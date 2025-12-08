package com.xypha.onlineBus.buses.Mapper;

import com.xypha.onlineBus.buses.Dto.BusResponse;
import com.xypha.onlineBus.buses.Entity.Bus;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BusMapper {

    @Insert ("INSERT INTO bus(bus_number, bus_type, total_seats, has_ac, has_wifi, img_url, description, price_per_km, driver_id, assistant_id, created_at, updated_at)"
    + "VALUES(#{busNumber}, #{busType}, #{totalSeats}, #{hasAC}, #{hasWifi}, #{imgUrl}, #{description}, #{pricePerKm}, #{driverId}, #{assistantId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertBus(Bus bus);

//    @Select("SELECT * FROM bus")
//    @Results({
//            @Result(property="id", column="id"),
//            @Result(property="busNumber", column="bus_number"),
//            @Result(property="busType", column="bus_type"),
//            @Result(property="totalSeats", column="total_seats"),
//            @Result(property="hasAC", column="has_ac"),
//            @Result(property="hasWifi", column="has_wifi"),
//            @Result(property="imgUrl", column="img_url"),
//            @Result(property="description", column="description"),
//            @Result(property="createdAt", column="created_at"),
//            @Result(property="updatedAt", column="updated_at"),
//            @Result(property="driverId", column="driver_id"),
//            @Result(property="driverName", column="driver_name"),
//            @Result(property="driverEmployeeId", column="driver_employee_id"),
//            @Result(property="assistantId", column="assistant_id"),
//            @Result(property="assistantName", column="assistant_name"),
//            @Result(property="assistantEmployeeId", column="assistant_employee_id")
//    })
//    List<Bus> getAllBuses();

    @Select("SELECT * FROM bus WHERE id = #{id}")
    @Results({
            @Result(property="id", column="id"),
            @Result(property="busNumber", column="bus_number"),
            @Result(property="busType", column="bus_type"),
            @Result(property="totalSeats", column="total_seats"),
            @Result(property="hasAC", column="has_ac"),
            @Result(property="hasWifi", column="has_wifi"),
            @Result(property="imgUrl", column="img_url"),
            @Result(property="description", column="description"),
            @Result(property="pricePerKm", column="price_per_km"),
            @Result(property="createdAt", column="created_at"),
            @Result(property="updatedAt", column="updated_at"),
            @Result(property="driverId", column="driver_id"),
            @Result(property="driverName", column="driver_name"),
            @Result(property="driverEmployeeId", column="driver_employee_id"),
            @Result(property="assistantId", column="assistant_id"),
            @Result(property="assistantName", column="assistant_name"),
            @Result(property="assistantEmployeeId", column="assistant_employee_id")
    })
    Bus getBusById(long id);

    @Update("UPDATE bus SET bus_number = #{busNumber}, bus_type = #{busType}, total_seats = #{totalSeats}, has_ac = #{hasAC}, has_wifi = #{hasWifi}, img_url = #{imgUrl}, description = #{description}, price_per_km =#{pricePerKm} ,driver_id = #{driverId}, assistant_id = #{assistantId}, updated_at = NOW() WHERE id = #{id}")
    void updateBus(Bus bus);

    @Delete("DELETE FROM bus WHERE id = #{id}")
    void deleteBus(Long id);

    @Select("SELECT COUNT(*) FROM bus WHERE bus_number = #{busNumber}")
    int existsByBusNumber (String busNumber);

    //Pagination
    @Select("""
        SELECT 
        b.id AS bus_id,
        b.bus_number, 
        b.bus_type, 
        b.total_seats,
        b.has_ac, 
        b.has_wifi, 
        b.img_url, 
        b.description,
        b.price_per_km,
        b.created_at,
        b.updated_at,

        d.id AS driver_id,
        d.name AS driver_name,
        d.phone_number AS driver_phone_number,
        d.license_number AS driver_license_number,
        d.employee_id AS driver_employee_id,

        a.id AS assistant_id,
        a.name AS assistant_name,
        a.phone_number AS assistant_phone_number,
        a.employee_id AS assistant_employee_id 

        FROM bus b
        LEFT JOIN driver d ON b.driver_id = d.id
        LEFT JOIN assistant a ON b.assistant_id = a.id
        ORDER BY b.id DESC
        LIMIT #{limit} OFFSET #{offset};
        """)
    @Results({
            @Result(property="id", column="bus_id"),
            @Result(property="busNumber", column="bus_number"),
            @Result(property="busType", column="bus_type"),
            @Result(property="totalSeats", column="total_seats"),
            @Result(property="hasAC", column="has_ac"),
            @Result(property="hasWifi", column="has_wifi"),
            @Result(property="imgUrl", column="img_url"),
            @Result(property="description", column="description"),
            @Result(property="pricePerKm", column="price_per_km"),
            @Result(property="createdAt", column="created_at"),
            @Result(property="updatedAt", column="updated_at"),

            // Driver nested
            @Result(property = "driver.id", column = "driver_id"),
            @Result(property = "driver.name", column = "driver_name"),
            @Result(property = "driver.phoneNumber", column = "driver_phone_number"),
            @Result(property = "driver.licenseNumber", column = "driver_license_number"),
            @Result(property = "driver.employeeId", column = "driver_employee_id"),

            // Assistant nested
            @Result(property = "assistant.id", column = "assistant_id"),
            @Result(property = "assistant.name", column = "assistant_name"),
            @Result(property = "assistant.phoneNumber", column = "assistant_phone_number"),
            @Result(property = "assistant.employeeId", column = "assistant_employee_id")
    })
    List<BusResponse> findPaginated(
            @Param("offset") int offset,
            @Param("limit") int limit);


    @Select("SELECT COUNT(*) FROM bus")
    int countBuses();


    //Validate to A driver or assistant cannot be assigned to more than one route on the same day.
    @Select("SELECT COUNT (*) FROM bus WHERE driver_id = #{driverId} AND DATE(created_at) = #{date}")
    int countDriverAssignmentsForDate(
            @Param("driverId") Long driverId,
            @Param("date") LocalDate date);


    @Select("SELECT COUNT (*) FROM bus WHERE assistant_id = #{assistantId} AND DATE(created_at) = #{date}")
    int countAssistantAssignmentsForDate(
            @Param("assistantId") Long assistantId,
            @Param("date") LocalDate date);



    @Select("SELECT COUNT(*) FROM bus WHERE driver_id = #{driverId}")
    int countBusesByDriverId (@Param("driverId") Long driverId);

    @Select("SELECT COUNT(*) FROM bus WHERE assistant_id = #{assistantId}")
    int countBusesByAssistantId (@Param("assistantId") Long assistantId);

    @Select("SELECT MAX(price_per_km) FROM bus WHERE bus_type = #{busType}")
    Double getMaxPricePerKmByType(@Param("busType") String busType);


}
