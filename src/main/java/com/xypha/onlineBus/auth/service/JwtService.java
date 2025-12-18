package com.xypha.onlineBus.auth.service;


import com.xypha.onlineBus.account.users.dto.UserResponse;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final String SECRET_KEY =
            "b5e9d2a1f8e4c7a3b6d9e1f4c8a2b7e3f1d9c6a8e5b4d2f7a1c9e8b6";

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
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
                .claim("role", userResponse.getRole().name())
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
            e.printStackTrace();
            return false;
        }
    }


    @Component
    public class DbWarmUp implements CommandLineRunner{

        private final JdbcTemplate jdbcTemplate;
        public DbWarmUp (JdbcTemplate jdbcTemplate){
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public void run(String... args){
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            System.out.println("Database connection established successfully.");
        }
    }




}
