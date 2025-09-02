package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.user.UserResponse;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(@AuthenticationPrincipal UserDetail userDetail) {
        List<UserResponse> users = userService.getAllUsers(userDetail);
        return ResponseEntity.ok(users);
    }
}
