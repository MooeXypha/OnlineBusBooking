package com.xypha.onlineBus.token.service;

import com.xypha.onlineBus.account.users.entity.User;
import com.xypha.onlineBus.account.users.mapper.UserMapper;
import com.xypha.onlineBus.token.dto.RefreshTokenResponse;
import com.xypha.onlineBus.token.entity.RefreshToken;
import com.xypha.onlineBus.token.mapper.RefreshTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    @Autowired
    private UserMapper userMapper;

    private static final long REFRESH_TOKEN_DURATION_DAYS = 7;

    //Generate a new fresh token and save to DB

    public String generateRefreshToken (User user){
        if (user == null || user.getId() == null){
            System.err.println("Cannot generate refresh token for null user");
            return null;
        }
        try {
            String token = UUID.randomUUID().toString();
            Instant expiryDate = Instant.now().plusSeconds(REFRESH_TOKEN_DURATION_DAYS * 24 * 60 * 60);

            refreshTokenMapper.insertRefreshToken(token, user.getId(), expiryDate);
            System.out.println("Generated refresh token for user ID: " + user.getId());
            return token;
        }catch (Exception e){
            System.err.println("Error generating refresh token: " + e.getMessage());
            return null;
        }
    }

    public User validateRefreshToken (String token){
        RefreshToken refreshToken = refreshTokenMapper.selectByToken(token);
        if (refreshToken == null){
            throw new RuntimeException("Refresh token not found");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())){

            refreshTokenMapper.deleteById(refreshToken.getId());
            throw new RuntimeException("Refresh token expired");
        }

        //Fetch user form DB
        User user = userMapper.getUserById(refreshToken.getUserId());
        if (user == null){
            throw new RuntimeException("User Not Found");
        }
        return user;
    }


    public void deleteRefreshToken (Long id){
        try {
            refreshTokenMapper.deleteById(id);
        }catch (Exception e){
            System.err.println("Error deleting refresh token: " + e.getMessage());
        }
    }

}
