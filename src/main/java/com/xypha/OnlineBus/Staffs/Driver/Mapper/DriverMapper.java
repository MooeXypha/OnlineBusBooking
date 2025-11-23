package com.xypha.OnlineBus.Staffs.Driver.Mapper;

import com.xypha.OnlineBus.Staffs.Driver.Entity.Driver;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DriverMapper {

    @Insert("""
        INSERT INTO driver (name, phone_number, license_number, employee_id)
        VALUES (#{name}, #{phoneNumber}, #{licenseNumber}, #{employeeId})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertDriver(Driver driver);

    @Select("SELECT * FROM driver WHERE id = #{id}")
    @Results({
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="phoneNumber", column="phone_number"),
            @Result(property="licenseNumber", column="license_number"),
            @Result(property="employeeId", column="employee_id")
    })
    Driver getDriverById (Long id);

    @Select("SELECT * FROM driver WHERE employee_id = #{employeeId}")
    Driver getDriverByEmployeeId (String employeeId);

    @Select("SELECT * FROM driver")
    @Results({
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="phoneNumber", column="phone_number"),
            @Result(property="licenseNumber", column="license_number"),
            @Result(property="employeeId", column="employee_id")
    })
    List<Driver> getALlDriver();

    @Update("""
            UPDATE driver
            SET name = #{name},
            phone_number = #{phoneNumber},
            license_number = #{licenseNumber},
            employee_id = #{employeeId}
            WHERE id = #{id}
            """)
    void updateDriver(Driver driver);

    @Delete("DELETE FROM driver WHERE id = #{id}")
    void deleteDriver(Long id);


}
