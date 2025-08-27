package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.user.JwtResponse;
import com.femcoders.pettrack.dtos.user.LoginRequest;
import com.femcoders.pettrack.dtos.user.UserRequest;
import com.femcoders.pettrack.dtos.user.UserResponse;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.security.jwt.JwtService;
import com.femcoders.pettrack.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.identifier(), loginRequest.password())
        );
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetail);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request){
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
