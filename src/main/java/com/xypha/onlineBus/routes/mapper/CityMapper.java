package com.xypha.onlineBus.routes.mapper;

import com.xypha.onlineBus.routes.Dto.CityResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CityMapper {

    @Select("SELECT name FROM city WHERE id = #{id}")
    String getCityNameById(Long id);

    @Select("SELECT * FROM city")
    List<CityResponse> getAllCities();


}
