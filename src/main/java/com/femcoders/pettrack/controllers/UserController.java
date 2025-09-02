package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordRequest;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.dtos.user.UserRequest;
import com.femcoders.pettrack.dtos.user.UserResponse;
import com.femcoders.pettrack.dtos.user.UserUpdateRequest;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUserById(@AuthenticationPrincipal UserDetail userDetail) {
        List<UserResponse> users = userService.getAllUsers(userDetail);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetail userDetail) {
        UserResponse userResponse = userService.getUserById(id, userDetail);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping()
    public ResponseEntity<UserResponse> createUser(
            @RequestBody @Valid UserRequest userRequest,
            @AuthenticationPrincipal UserDetail userDetail) {
        UserResponse userResponse = userService.createUser(userRequest, userDetail);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateMedicalRecord(@PathVariable Long id,
                                                                     @RequestBody @Valid UserUpdateRequest userUpdateRequest,
                                                                     @AuthenticationPrincipal UserDetail userDetail) {
        UserResponse updatedUser = userService.updateUser(id, userUpdateRequest, userDetail);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateMedicalRecord(@PathVariable Long id,
                                                                   @AuthenticationPrincipal UserDetail userDetail) {
        Map<String, String> response = userService.deleteUser(id, userDetail);
        return ResponseEntity.ok(response);
    }
}
