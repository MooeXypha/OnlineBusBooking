package com.xypha.onlineBus.staffs.Driver.Mapper;

import com.xypha.onlineBus.staffs.Driver.Dto.DriverResponse;
import com.xypha.onlineBus.staffs.Driver.Entity.Driver;
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
    Driver getDriverById(Long id);

    @Select("SELECT * FROM driver WHERE employee_id = #{employeeId}")
    Driver getDriverByEmployeeId(String employeeId);

    @Select("SELECT * FROM driver ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="phoneNumber", column="phone_number"),
            @Result(property="licenseNumber", column="license_number"),
            @Result(property="employeeId", column="employee_id")
    })
    List<DriverResponse> getAllPaginatedDriver(
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
            SELECT * FROM driver
            """)
    @Results({
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="phoneNumber", column="phone_number"),
            @Result(property="licenseNumber", column="license_number"),
            @Result(property="employeeId", column="employee_id")
    })
    List<Driver> getAllDrivers();

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

    @Select("SELECT COUNT(*) FROM driver WHERE employee_id = #{employeeId}")
    int countEmployeeId(String employeeId);

    @Select("SELECT COUNT(*) FROM driver")
    long countDrivers();

    @Select("""
        SELECT COUNT(*) FROM driver WHERE employee_id = #{employeeId} AND id != #{id}
    """)
    int countDriverUpdate(@Param("employeeId") String employeeId,
                          @Param("id") Long id);

    @Select("SELECT * FROM driver WHERE name = #{name}")
    @Results({
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="phoneNumber", column="phone_number"),
            @Result(property="licenseNumber", column="license_number"),
            @Result(property="employeeId", column="employee_id")
    })
    Driver findByName(String name);

    @Select("SELECT * FROM driver WHERE phone_number = #{phoneNumber}")
    Driver findByPhoneNumber(String phoneNumber);

    @Select("SELECT * FROM driver WHERE license_number = #{licenseNumber}")
    Driver findByLicenseNumber(String licenseNumber);

    @Select("""
        SELECT * FROM driver WHERE name = #{name}
        ORDER BY id DESC
        LIMIT #{limit} OFFSET #{offset}
    """)
    @Results({
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="phoneNumber", column="phone_number"),
            @Result(property="licenseNumber", column="license_number"),
            @Result(property="employeeId", column="employee_id")
    })
    List<Driver> searchDriverByName(@Param("name") String name,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM driver WHERE name = #{name}")
    long countDriverByName(@Param("name") String name);
}