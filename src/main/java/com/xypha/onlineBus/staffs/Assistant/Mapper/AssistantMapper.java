package com.xypha.onlineBus.staffs.Assistant.Mapper;

import com.xypha.onlineBus.staffs.Assistant.Dto.AssistantResponse;
import com.xypha.onlineBus.staffs.Assistant.Entity.Assistant;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AssistantMapper {

    @Insert("""
        INSERT INTO assistant (name, phone_number, employee_id)
        VALUES (#{name}, #{phoneNumber}, #{employeeId})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertAssistant(Assistant assistant);

    @Select("SELECT * FROM assistant ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="phoneNumber", column="phone_number"),
            @Result(property="employeeId", column="employee_id")
    })
    List<AssistantResponse> getAllAssistantPaginated(@Param("offset") int offset,
                                                     @Param("limit") int limit);

    @Select("SELECT * FROM assistant WHERE id = #{id}")
    @Results({
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="phoneNumber", column="phone_number"),
            @Result(property="employeeId", column="employee_id")
    })
    Assistant getAssistantById(Long id);

    @Select("SELECT * FROM assistant WHERE employee_id = #{employeeId}")
    Assistant getAssistantByEmployeeId(String employeeId);

    @Update("""
        UPDATE assistant 
        SET name = #{name},
            phone_number = #{phoneNumber},
            employee_id = #{employeeId}
        WHERE id = #{id}
    """)
    void updateAssistant(Assistant assistant);

    @Delete("DELETE FROM assistant WHERE id = #{id}")
    void deleteAssistant(Long id);

    @Select("SELECT COUNT(*) FROM assistant WHERE employee_id = #{employeeId}")
    int countEmployeeId(String employeeId);

    @Select("""
        SELECT COUNT(*) FROM assistant WHERE employee_id = #{employeeId} AND id != #{id}
    """)
    int countAssistantUpdate(@Param("employeeId") String employeeId,
                             @Param("id") Long id);

    @Select("SELECT * FROM assistant WHERE name = #{name} ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="phoneNumber", column="phone_number"),
            @Result(property="employeeId", column="employee_id")
    })
    List<Assistant> searchAssistantByName(@Param("name") String name,
                                                  @Param("offset") int offset,
                                                  @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM assistant")
    long countAssistants();

    @Select("SELECT COUNT(*) FROM assistant WHERE name = #{name}")
    long countAssistantByName(@Param("name") String name);

    @Select("SELECT * FROM assistant WHERE name = #{name}")
    Assistant findByName(String name);

    @Select("SELECT * FROM assistant WHERE phone_number = #{phoneNumber}")
    Assistant findByPhoneNumber(String phoneNumber);
}