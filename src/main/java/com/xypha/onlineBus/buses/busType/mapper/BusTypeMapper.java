package com.xypha.onlineBus.buses.busType.mapper;

import com.xypha.onlineBus.buses.busType.dto.BusTypeRequest;
import com.xypha.onlineBus.buses.busType.entity.BusType;
import com.xypha.onlineBus.buses.services.Service;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BusTypeMapper {

    @Insert("INSERT INTO bus_type (name,seat_per_row) VALUES (#{name}, #{seatPerRow})")
    @Options(useGeneratedKeys = true, keyProperty = "id")

    void insertBusType(BusType busType);

    @Select("SELECT * FROM bus_type ORDER BY created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "seatPerRow", column = "seat_per_row")
    })
    List<BusType> getAllBusTypes();



    @Select("SELECT COUNT(*) FROM bus_type")
    int countBusTypes();

    @Select("SELECT * FROM bus_type WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "seatPerRow", column = "seat_per_row")

    })
    BusType getBusTypeById(long id);

    @Update("UPDATE bus_type SET name = #{name} , seatPerRow = #{seatPerRow} WHERE id = #{id}")
    void updateBusType(BusType busType);

    @Delete("DELETE FROM bus_type WHERE id = #{id}")
    void deleteBusType(long id);

    @Select("SELECT s.* FROM service s JOIN bus_type_service bts ON s.id = bts.service_id WHERE bts.bus_type_id = #{busTypeId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "seatPerRow", column = "seat_per_row")

    })
    List<Service>findServicesByBusTypeId(@Param("busTypeId") Long busTypeId);

    @Insert({
            "<script>",
            "INSERT INTO bus_type_service(bus_type_id, service_id) VALUES",
            "<foreach collection='serviceIds' item='serviceId' separator=','>",
            "(#{busTypeId}, #{serviceId})",
            "</foreach>",
            "</script>"
    })
    void addServicesToBusType(@Param("busTypeId") Long busTypeId, @Param("serviceIds") List<Long> serviceIds);

    @Delete("DELETE FROM bus_type_service WHERE bus_type_id = #{busTypeId}")
    void removeServicesFromBusType(Long busTypeId);




}
