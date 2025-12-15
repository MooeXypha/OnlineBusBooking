package com.xypha.onlineBus.buses.busTypeLayout.mapper;

import com.xypha.onlineBus.buses.busTypeLayout.entity.BusTypeLayout;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BusTypeLayoutMapper {

    @Select("SELECT * FROM bus_type_layout WHERE bus_type_id = #{busTypeId}")
    BusTypeLayout getBusTypeId (@Param("busTypeId") Long busTypeId);

}
