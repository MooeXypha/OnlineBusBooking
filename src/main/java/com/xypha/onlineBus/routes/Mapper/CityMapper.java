package com.xypha.onlineBus.routes.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CityMapper {

    @Select("SELECT name FROM city WHERE id = #{id}")
    String getCityNameById(Long id);
}
