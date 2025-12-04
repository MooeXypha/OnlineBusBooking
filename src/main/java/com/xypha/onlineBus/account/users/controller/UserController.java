package com.xypha.onlineBus.account.users.controller;

import com.xypha.onlineBus.account.users.dto.UserRequest;
import com.xypha.onlineBus.account.users.dto.UserResponse;
import com.xypha.onlineBus.account.users.entity.User;
import com.xypha.onlineBus.account.users.service.CustomUserDetails;
import com.xypha.onlineBus.account.users.service.UserService;
import com.xypha.onlineBus.api.ApiResponse;
import com.xypha.onlineBus.api.PaginatedResponse;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin ("*")
@RestController
@RequestMapping ("/api/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {

        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest userRequest) {
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getAllUsers(
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue = "10") int limit
    ) {
        PaginatedResponse<UserResponse> paginatedResponse = userService.getAllUser(offset, limit);
        ApiResponse<PaginatedResponse<UserResponse>> response = new ApiResponse<>(
                "SUCCESS",
                "Users retrieved successfully",
                paginatedResponse
        );
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    //FOR SUPER ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

//        System.out.println("JWT user ID:" +userDetails.getId());
//        System.out.println("Target ID From URl:" + id);
//        System.out.println("Authorities: "+userDetails.getAuthorities() );

        boolean isSuperAdmin = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("SUPER_ADMIN"));
        boolean isOwner = userDetails.getId().equals(id);

        if (!isSuperAdmin && !isOwner){
        ApiResponse<UserResponse> forbiddenResponse = new ApiResponse<>(
                "FORBIDDEN",
                "You are not authorized to update this user",
                null
        );
        return ResponseEntity.status(403).body(forbiddenResponse);
        }

        UserResponse updatedUser = userService.updateUser(id, userRequest);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                "SUCCESS",
                "User updated successfully: " + id,
                updatedUser
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser (
            @PathVariable Long id
    ){
        userService.deleteUser(id);
        ApiResponse<Void> response = new ApiResponse<>("SUCCESS", "User deleted successfully: " + id, null);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> searchUsers(
            @RequestParam String gmail,
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue = "10") int limit
    ){
        PaginatedResponse<UserResponse> paginatedResponse = userService.searchUserByGmail(gmail, offset, limit);
        if (paginatedResponse.getContents().isEmpty()){
            ApiResponse<PaginatedResponse<UserResponse>> response = new ApiResponse<>(
                    "NOT_FOUND",
                    "No users found with gmail: " + gmail,
                    null
            );
            return ResponseEntity.status(404).body(response);
        }


        ApiResponse<PaginatedResponse<UserResponse>> response = new ApiResponse<>(
                "SUCCESS",
                "Search results for gmail: " + gmail,
                paginatedResponse
        );
            return ResponseEntity.ok(response);
    }

}


