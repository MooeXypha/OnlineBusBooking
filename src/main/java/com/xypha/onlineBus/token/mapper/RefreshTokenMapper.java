package com.xypha.onlineBus.token.mapper;

import com.xypha.onlineBus.token.entity.RefreshToken;
import org.apache.ibatis.annotations.*;

import java.time.Instant;

@Mapper
public interface RefreshTokenMapper {

    @Insert("""
            INSERT INTO refresh_tokens (token, user_id, expiry_date) 
            VALUES(#{token}, #{userId}, #{expiryDate})
            """)
    void insertRefreshToken (
            @Param("token") String token,
            @Param("userId") Long userId,
            @Param("expiryDate")Instant expiryDate
            );


    @Select("SELECT * FROM refresh_tokens WHERE token = #{token}")
    RefreshToken selectByToken(
            @Param("token") String token
    );

    @Delete("DELETE FROM refresh_tokens WHERE id = #{id}")
    void deleteById (@Param("id") Long id);

}
