package com.xypha.onlineBus.account.users.controller;

import com.xypha.onlineBus.account.users.dto.UserRequest;
import com.xypha.onlineBus.account.users.dto.UserResponse;
import com.xypha.onlineBus.account.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest userRequest){
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal String username){
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal String username,
            @RequestBody UserRequest request
    ){
        return ResponseEntity.ok(userService.updateUserByUsername(username,request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUser();
        return ResponseEntity.ok(users);

    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        UserResponse user = userService.getUserById(id);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    //FOR SUPER ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
             @Valid @RequestBody UserRequest userRequest){
        UserResponse updated = userService.updateUser(id, userRequest);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.ok("User delete Successful")
                : ResponseEntity.notFound().build();
    }



}
