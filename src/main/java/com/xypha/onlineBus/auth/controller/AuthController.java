package com.xypha.onlineBus.auth.controller;

import com.xypha.onlineBus.account.representative.RepreRequest;
import com.xypha.onlineBus.account.users.dto.UserRequest;
import com.xypha.onlineBus.account.users.dto.UserResponse;
import com.xypha.onlineBus.account.users.entity.User;
import com.xypha.onlineBus.account.users.service.CustomUserDetails;
import com.xypha.onlineBus.account.users.service.UserService;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.auth.dto.AuthRequest;
import com.xypha.onlineBus.auth.service.JwtService;
import com.xypha.onlineBus.restPassword.Service.AuthService;
import com.xypha.onlineBus.restPassword.entity.RestToken;
import com.xypha.onlineBus.token.dto.RefreshTokenRequest;
import com.xypha.onlineBus.token.dto.RefreshTokenResponse;
import com.xypha.onlineBus.token.service.RefreshTokenService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;



    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController (AuthenticationManager authenticationManager, JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody AuthRequest authRequest) {


        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            // Get user details from DB after authentication
            UserResponse user = userService.getUserByUsername(auth.getName());

            // Generate JWT token
            String token = jwtService.generateToken(user);


            //Refresh token and save to DB
            String refreshToken = null;
            try {
                User userEntity = userService.getUserEntityByUsername(user.getUsername());
                if (userEntity != null) {
                    refreshToken = refreshTokenService.generateRefreshToken(userEntity);
                }
            }catch (Exception e) {
                System.err.println("Error generating refresh token: " + e.getMessage());
            }

            // Prepare payload
            Map<String, Object> payload = Map.of(
                    "user", user,
                    "accessToken", token,
                    "refreshToken", refreshToken

            );
            ApiResponse<Map<String, Object>>response = new ApiResponse<>();
            response.setStatus("SUCCESS");
            response.setMessage("Login Successful");
            response.setPayload(payload);
            response.setTimestamp(java.time.LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Map<String, Object>>response = new ApiResponse<>();
            response.setStatus("FAILED");
            response.setMessage("Invalid username or password");
            response.setPayload(null);
            response.setTimestamp(java.time.LocalDateTime.now());

            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/login/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> logIntoDashboard(@RequestBody RepreRequest request) {
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        try {
            // Step 1: Authenticate username & password
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Step 2: Get user details from DB
            UserResponse user = userService.getUserByUsername(auth.getName());

            // Step 3: Check if role matches
            if (user.getRole() != request.getRole()) {
                response.setStatus("FAILED");
                response.setMessage("Role selection is incorrect for this user");
                response.setPayload(null);
                response.setTimestamp(java.time.LocalDateTime.now());
                return ResponseEntity.status(403).body(response); // 403 Forbidden
            }

            // Step 4: Generate JWT token
            String token = jwtService.generateToken(user);

            // Step 5: Generate refresh token
            String refreshToken = null;
            try {
                User userEntity = userService.getUserEntityByUsername(user.getUsername());
                if (userEntity != null) {
                    refreshToken = refreshTokenService.generateRefreshToken(userEntity);
                }
            } catch (Exception e) {
                System.err.println("Error generating refresh token: " + e.getMessage());
            }

            // Step 6: Prepare payload
            Map<String, Object> payload = Map.of(
                    "user", user,
                    "accessToken", token,
                    "refreshToken", refreshToken
            );

            response.setStatus("SUCCESS");
            response.setMessage("Login Successful");
            response.setPayload(payload);
            response.setTimestamp(java.time.LocalDateTime.now());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            // Wrong username or password
            response.setStatus("FAILED");
            response.setMessage("Invalid username or password");
            response.setPayload(null);
            response.setTimestamp(java.time.LocalDateTime.now());
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            // Other exceptions
            response.setStatus("FAILED");
            response.setMessage("Login failed: " + e.getMessage());
            response.setPayload(null);
            response.setTimestamp(java.time.LocalDateTime.now());
            return ResponseEntity.status(500).body(response);
        }
    }




    @PostMapping ("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerUser (
            @Valid @RequestBody UserRequest userRequest
            ){
        UserResponse savedUser = userService.createUser(userRequest);

        SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(savedUser.getRole().name());
        String token = jwtService.generateToken(savedUser);

        Map<String, Object> payload = Map.of(
                "user" , savedUser,
                    "token" ,token

        );
        ApiResponse<Map<String, Object>>response = new ApiResponse<>("SUCCESS","User registered successfully",payload);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/debug-login")
    public ResponseEntity<?> debugLogin(@RequestBody AuthRequest req) {
        try{
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getUsername(),
                            req.getPassword()
                    )
                    );
            return ResponseEntity.ok(Map.of("authenticated",auth.isAuthenticated()));

        }catch (Exception e){
            return ResponseEntity.status(401)
                    .body(Map.of("error","Invalid Username or password"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile (
            @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null){
        ApiResponse<UserResponse> unauthorized = new ApiResponse<>(
                "UNAUTHORIZED",
                "You are not authenticated",
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorized);
        }
        UserResponse user = userService.getUserById(userDetails.getId());
        ApiResponse<UserResponse> response = new ApiResponse<>(
                "SUCCESS",
                "User profile retrieved successfully",
                user
        );
        return ResponseEntity.ok(response);
    }
    


    @GetMapping("/reset-tokens")
    public ResponseEntity<?> getAllResetTokens(){
        List<RestToken> tokens = authService.getAllResetToken();
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken (
            @RequestBody RefreshTokenRequest request){
        try{
            User user = refreshTokenService.validateRefreshToken(request.getRefreshToken());

            UserResponse userResponse = userService.getUserById(user.getId());
            String newAccessToken = jwtService.generateToken(userResponse);
            String newRefreshToken = refreshTokenService.generateRefreshToken(user);

            RefreshTokenResponse responseData = new RefreshTokenResponse(newAccessToken, newRefreshToken);
            ApiResponse<RefreshTokenResponse> response = new ApiResponse<>(true,"Token refreshed successfully",responseData);

            return ResponseEntity.ok(response);
        }catch (RuntimeException e){
            System.out.println("Refersh Token Error"+ e.getMessage());
            ApiResponse<RefreshTokenResponse> response = new ApiResponse<>(false, e.getMessage(),null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }







}
