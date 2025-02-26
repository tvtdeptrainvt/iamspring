package com.example.database.controller;


import com.example.database.dto.request.ApiResponse;
import com.example.database.dto.request.ChangePasswordRequest;
import com.example.database.dto.request.UserUpdateRequest;
import com.example.database.dto.response.UserResponse;
import com.example.database.entity.UserEntity;
import com.example.database.dto.request.UserCreationRequest;
import com.example.database.service.AuthenticationService;
import com.example.database.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserEntity> createUser(@RequestBody @Valid UserCreationRequest request){
        ApiResponse<UserEntity> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }
    @GetMapping
    List<UserEntity> getUsers(){
        return userService.getUsers();
    }
    @GetMapping("/{userId}")
    UserResponse getUserEntity(@PathVariable("userId") int userId){
        return userService.getUserEntity(userId);
    }

    @PutMapping("/{userId}")
    UserResponse updateUserEntity(@PathVariable int userId, @RequestBody UserUpdateRequest request){
        return userService.updateUserEntity(userId, request);
    }

    @DeleteMapping("/{userId}")
        String deleteUserEntity(@PathVariable int userId){
        userService.deleteUserEntity(userId);
        return "UserEntity has delete";
    }
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable int userId,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }
}
