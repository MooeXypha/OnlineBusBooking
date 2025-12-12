package com.xypha.onlineBus.buses.services;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ServiceMapper {

    @Insert("INSERT INTO service (name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertService(Service service);



    @Select("""
            SELECT s.id, s.name
            FROM service s
            ORDER BY s.id DESC
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name")
    })
    List<Service> getAllServices();

    @Select("SELECT * FROM service WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name")

    })
    Service getServiceById(long id);

    @Update("UPDATE service SET name = #{name} WHERE id = #{id}")
    void updateService(Service service);

    @Delete("DELETE FROM service WHERE id = #{id}")
    void deleteService(long id);

    @Select("SELECT COUNT (*) FROM service")
    int countServices();

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM service WHERE id IN",
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int countExistingServices(@Param("ids") List<Long> ids);


}
