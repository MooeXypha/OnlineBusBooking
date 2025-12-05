package com.xypha.onlineBus.auth.service;


import com.xypha.onlineBus.account.users.dto.UserResponse;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final String SECRET_KEY = "very_long_and_secure_secret_key_at_least_32_bytes";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private static final long EXPIRATION_MS = 3600000;
    public String generateToken (UserResponse userResponse){

        Map<String, List<String>> modules = new HashMap<>();
        switch (userResponse.getRole()){
            case SUPER_ADMIN -> {
                modules.put("bus", List.of("VIEW","CREATE","EDIT","DELETE"));
                modules.put("route", List.of("VIEW","CREATE","EDIT","DELETE"));
                modules.put("staff", List.of("VIEW","CREATE","EDIT","DELETE"));
                modules.put("user", List.of("VIEW","CREATE","EDIT","DELETE"));
                modules.put("tickerBooking", List.of("VIEW","CREATE","UPDATE","DELETE"));
            }
            case ADMIN -> {
                modules.put("bus", List.of("VIEW"));
                modules.put("route", List.of("VIEW", "CREATE", "UPDATE"));
                modules.put("user", List.of("VIEW"));
                modules.put("staff", List.of("VIEW", "CREATE", "UPDATE"));
                modules.put("ticketBooking", List.of("VIEW", "CREATE", "UPDATE"));
            }
            case USER -> {
                modules.put("route", List.of("VIEW"));
                modules.put("ticketBooking", List.of("CREATE", "CANCEL"));
            }
            case RECEPTION -> {
                modules.put("bus", List.of("VIEW"));
                modules.put("route", List.of("VIEW"));
                modules.put("ticketBooking", List.of("CREATE", "CANCEL"));
            }
        }

        Map<String, Object> authorities = new HashMap<>();
        authorities.put("role", userResponse.getRole().name());
        authorities.put("modules", modules);



        return Jwts.builder()
                .setSubject(userResponse.getUsername())
                .claim("authorities", authorities)
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
