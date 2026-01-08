package com.xypha.onlineBus.account.users.mapper;

import com.xypha.onlineBus.account.role.Role;
import com.xypha.onlineBus.account.users.dto.UserResponse;
import com.xypha.onlineBus.account.users.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    //Validation
    //Email Search
    @Select("""
            SELECT * FROM users WHERE gmail LIKE CONCAT ('%', #{gmail}, '%')
            ORDER BY id DESC 
            LIMIT #{limit} OFFSET #{offset}
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "gmail", column = "gmail"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "nrc", column = "nrc"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "dob", column = "dob"),
            @Result(property = "citizenship", column = "citizenship"),
            @Result(property = "role", column = "role" , javaType = Role.class)
    })
    List<UserResponse> searchUserByEmail (
            @Param("gmail") String gmail,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
            SELECT COUNT(*) FROM users WHERE gmail LIKE CONCAT ('%', #{gmail}, '%')
            """)
    long countUsersByGmail (
            @Param("gmail") String gmail
    );


    //PhoneNumber Search
    @Select(" SELECT * FROM users WHERE phone_number = #{phoneNumber} ")
    User findByPhoneNumber (String phoneNumber);

    //NRC search
    @Select(" SELECT * FROM users WHERE nrc = #{nrc} ")
    User findByNrc (String nrc);
    @Insert("""
            INSERT INTO users (username, password, gmail, phone_number, nrc, gender, dob, citizenship, role)
            VALUES (#{username}, #{password}, #{gmail}, #{phoneNumber}, #{nrc}, #{gender}, #{dob}, #{citizenship}, #{role})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Select("""
            SELECT * FROM users WHERE id = #{id}
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "gmail", column = "gmail"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "nrcOrPassport", column = "nrc_or_passport"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "dob", column = "dob"),
            @Result(property = "citizenship", column = "citizenship"),
            @Result(property = "role" , column = "role")

    })
    User getUserById(Long id);

    @Select("""
            SELECT u.*
            FROM users u
              ORDER BY u.id DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "gmail", column = "gmail"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "nrc", column = "nrc"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "dob", column = "dob"),
            @Result(property = "citizenship", column = "citizenship"),
            @Result(property = "role", column = "role" , javaType = Role.class),

    })
    List<UserResponse> getAllUsersPaginated(
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("SELECT COUNT(*) FROM users")
    long countUsers();

    @Select("SELECT * FROM users WHERE username = #{username}")
    User getUserByUsername (String username);

    @Update("""
            UPDATE users SET
            username = #{username},
            password = #{password},
            gmail = #{gmail},
            phone_number = #{phoneNumber},
            nrc = #{nrc},
            gender = #{gender},
            dob = #{dob},
            citizenship = #{citizenship},
            role = #{role}
            WHERE id = #{id}
            """)
    void updateUser (User user);

    @Delete("DELETE FROM users WHERE id = #{id} ")
    void deleteUser(Long id);

    @Select(" SELECT * FROM users WHERE username = #{username}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "gmail", column = "gmail"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "nrc", column = "nrc"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "dob", column = "dob"),
            @Result(property = "citizenship", column = "citizenship"),
            @Result(property = "role", column = "role")
    })
    User findByUsername (String username)
;

    //For forget password part
    @Update("Update users SET password = #{password} WHERE id = #{id}")
    void updatePassword (
            @Param("id") Long id,
            @Param("password") String password
    );

    @Select("SELECT * FROM users WHERE gmail = #{gmail}")
    User findByEmail (@Param("gmail") String gmail);


    ////////////////////////////role filter
    @Select("""
    <script>
    SELECT *
    FROM users u
    <where>
        <if test='role != null'>
            u.role = #{role}
        </if>
    </where>
    ORDER BY u.id DESC
    LIMIT #{limit} OFFSET #{offset}
    </script>
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "gmail", column = "gmail"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "nrc", column = "nrc"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "dob", column = "dob"),
            @Result(property = "citizenship", column = "citizenship"),
            @Result(property = "role", column = "role", javaType = Role.class)
    })
    List<User> getUsersByRolePaginated(
            @Param("role") String role,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM users u
        <where>
            <if test='role != null'>
                u.role = #{role}
            </if>
        </where>
        </script>
        """)
    int countUserByRole(@Param("role") String role);

    @Select("SELECT gmail FROM users WHERE id = #{id}")
    String getEmailById (@Param("id") Long id);

    @Select("SELECT userName FROM users WHERE id = #{id}")
    String getNameById (@Param("id") Long id);

}
