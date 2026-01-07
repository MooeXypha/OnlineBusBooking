package com.xypha.onlineBus.config;

import com.xypha.onlineBus.account.users.mapper.UserMapper;
import com.xypha.onlineBus.account.users.service.UserService;
import com.xypha.onlineBus.auth.filter.JwtAuthFilter;
import com.xypha.onlineBus.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.apache.tomcat.util.http.Method.OPTIONS;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(UserDetailsService userDetailsService) {
        return new JwtAuthFilter(userDetailsService, jwtService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity, UserService userService)
            throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthFilter jwtAuthFilter)
            throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "https://onlinebusbooking.onrender.com",
                            "http://localhost:5173",
                            "http://localhost:63342",
                            "http://localhost:3000",
                            "https://cozy-bus.pages.dev",
                            "https://online-bus-booking-frontend.pages.dev/"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                        }))




                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        // Swagger
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        // Normal user endpoints
                        .requestMatchers("/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/reset-tokens")
                        .permitAll()
                        // Normal user endpoints
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/auth/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login/dashboard").permitAll()

                        // For Staff CRUD
                        .requestMatchers(HttpMethod.GET, "/api/staff/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/staff/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/staff/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/staff/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")

                        //Trip CRUD
                        .requestMatchers(HttpMethod.GET, "/api/trip/**").permitAll() //view trips
                        .requestMatchers(HttpMethod.GET, "/api/trip/paginated").permitAll() //view paginated trips
                        .requestMatchers(HttpMethod.POST, "/api/trip/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/trip/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/trip/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")

                        // ROUTE CRUD
                        .requestMatchers(HttpMethod.GET, "/api/route/**")
                        .hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/route/**")
                        .hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/route/**")
                        .hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/route/**")
                        .hasAnyAuthority("ADMIN", "SUPER_ADMIN")

                        // For Bus CRUD
                        .requestMatchers(HttpMethod.GET, "/api/bus/**").hasAnyAuthority("SUPER_ADMIN","ADMIN","RECEPTION") // view bus
                        .requestMatchers(HttpMethod.POST,"/api/bus/upload").hasAnyAuthority("SUPER_ADMIN","ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/bus/**").hasAnyAuthority("SUPER_ADMIN","ADMIN") // update bus
                        .requestMatchers(HttpMethod.DELETE, "/api/bus/**").hasAuthority("SUPER_ADMIN") // delete bus

                        // ADMIN scope(can view users)
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/users/search/**").hasAuthority("SUPER_ADMIN") //for the search
                        .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()

                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyAuthority("SUPER_ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("SUPER_ADMIN")


                        //seat
                        .requestMatchers(HttpMethod.GET, "/api/seat/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/seat/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/seat/**").hasAnyAuthority("SUPER_ADMIN","ADMIN","RECEPTION")
                        .requestMatchers(HttpMethod.DELETE,"/api/seat/**").hasAnyAuthority("SUPER_ADMIN","USER")

                        //Booking
                        .requestMatchers(HttpMethod.DELETE,"/api/booking/**").hasAnyAuthority("SUPER_ADMIN","ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/booking/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/booking/**").hasAnyAuthority("SUPER_ADMIN","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/bookings/history").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/booking/**").hasAnyAuthority("SUPER_ADMIN","ADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }



}
