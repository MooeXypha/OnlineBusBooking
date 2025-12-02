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

    public SecurityConfig(JwtService jwtService){
        this.jwtService = jwtService;
    }


    @Bean
    public JwtAuthFilter jwtAuthFilter(UserDetailsService userDetailsService){
        return new JwtAuthFilter(userDetailsService, jwtService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity, UserService userService) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,JwtAuthFilter jwtAuthFilter) throws Exception{
        httpSecurity.cors().and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        // Swagger
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()


                        //Public endpoints
                        //Normal user endpoints
                        .requestMatchers("/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/reset-tokens").permitAll()
                        //Normal user endpoints
                        .requestMatchers("/api/auth/me").authenticated()



                                //For Staff CRUD
                        .requestMatchers(HttpMethod.GET,"/api/staff/**").hasAnyAuthority("SUPER_ADMIN","ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/staff/**").hasAnyAuthority("SUPER_ADMIN","ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/staff/**").hasAnyAuthority("SUPER_ADMIN","ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/staff/**").hasAnyAuthority("SUPER_ADMIN","ADMIN")

                        //ROUTE CRUD
                        .requestMatchers(HttpMethod.GET,    "/api/route/**")
                        .hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/route/**")
                        .hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/route/**")
                        .hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/route/**")
                        .hasAnyAuthority("ADMIN", "SUPER_ADMIN")

                        //For Bus CRUD
                        .requestMatchers(HttpMethod.GET,"/api/bus/**").permitAll() //view bus
                        .requestMatchers("/api/bus/upload").hasAuthority("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/bus/**").hasAuthority("SUPER_ADMIN") //update bus
                        .requestMatchers(HttpMethod.DELETE,"/api/bus/**").hasAuthority("SUPER_ADMIN") //delete bus



                        //ADMIN scope(can view users)
                        .requestMatchers(HttpMethod.GET,"/api/users/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/users/**").hasAnyRole("ADMIN","SUPER_ADMIN")


                        //Super Admin endpoints (full control)
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/users/**").hasRole("SUPER_ADMIN")

                        .anyRequest().authenticated()
                ).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
        public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOriginPatterns("http://localhost:3000", "https://onlinebusbooking.onrender.com")
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
        }

    // Provide a CorsConfigurationSource bean so Spring Security's http.cors() uses these settings
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://localhost:63342",
                "https://onlinebusbooking.onrender.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
