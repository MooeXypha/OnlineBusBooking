package com.xypha.OnlineBus.Buses.Mapper;

import com.xypha.OnlineBus.Buses.Dto.BusResponse;
import com.xypha.OnlineBus.Buses.Entity.Bus;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BusMapper {

    @Insert ("INSERT INTO bus(bus_number, bus_type, total_seats, has_ac, has_wifi, img_url, description, driver_id, assistant_id, created_at, updated_at)"
    + "VALUES(#{busNumber}, #{busType}, #{totalSeats}, #{hasAC}, #{hasWifi}, #{imgUrl}, #{description}, #{driverId}, #{assistantId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertBus(Bus bus);

    @Select("SELECT * FROM bus")
    @Results({
            @Result(property="id", column="id"),
            @Result(property="busNumber", column="bus_number"),
            @Result(property="busType", column="bus_type"),
            @Result(property="totalSeats", column="total_seats"),
            @Result(property="hasAC", column="has_ac"),
            @Result(property="hasWifi", column="has_wifi"),
            @Result(property="imgUrl", column="img_url"),
            @Result(property="description", column="description"),
            @Result(property="createdAt", column="created_at"),
            @Result(property="updatedAt", column="updated_at"),
            @Result(property="driverId", column="driver_id"),
            @Result(property="driverName", column="driver_name"),
            @Result(property="driverEmployeeId", column="driver_employee_id"),
            @Result(property="assistantId", column="assistant_id"),
            @Result(property="assistantName", column="assistant_name"),
            @Result(property="assistantEmployeeId", column="assistant_employee_id")
    })
    List<Bus> getAllBuses();

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

    @Update("UPDATE bus SET bus_number = #{busNumber}, bus_type = #{busType}, total_seats = #{totalSeats}, has_ac = #{hasAC}, has_wifi = #{hasWifi}, img_url = #{imgUrl}, description = #{description}, driver_id = #{driverId}, assistant_id = #{assistantId}, updated_at = NOW() WHERE id = #{id}")
    void updateBus(Bus bus);

    @Delete("DELETE FROM bus WHERE id = #{id}")
    void deleteBus(Long id);

    @Select("SELECT COUNT(*) FROM bus WHERE bus_number = #{busNumber}")
    int existsByBusNumber (String busNumber);

    //Pagination
    @Select("SELECT * FROM bus ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    List<Bus> findPaginated(@Param("offset")int offset,
                            @Param("limit")int limit);

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


}
