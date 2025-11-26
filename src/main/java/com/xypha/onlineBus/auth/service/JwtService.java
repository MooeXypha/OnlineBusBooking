package com.xypha.onlineBus.auth.service;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final String SECRET_KEY = "very_long_and_secure_secret_key_at_least_32_bytes";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private static final long EXPIRATION_MS = 3600000;
    public String generateToken (String username, Collection <? extends GrantedAuthority> authorities ){

        String roles = authorities != null
                ? authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","))
                : "ROLE_USER";  //default role if no authorities provided


        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusMillis(EXPIRATION_MS)))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();

    }

    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();  //
    }

    public boolean validToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }


}
