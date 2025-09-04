package com.femcoders.pettrack.controllers;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordRequest;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.dtos.pet.PetResponse;
import com.femcoders.pettrack.dtos.user.UserRequest;
import com.femcoders.pettrack.dtos.user.UserResponse;
import com.femcoders.pettrack.dtos.user.UserUpdateRequest;
import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Endpoints for managing users in PetTrack")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all registered users with basic information such as username, email, and role." +
                    "This operation is restricted to users with the VETERINARY role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(@AuthenticationPrincipal UserDetail userDetail) {
        List<UserResponse> users = userService.getAllUsers(userDetail);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns a single user by its ID. Accessible only to users with role VETERINARY."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetail userDetail) {
        UserResponse userResponse = userService.getUserById(id, userDetail);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(
            summary = "Filter users by role",
            description = "Returns users filtered by role (optional). If no role is provided, returns all users. "
                    + "Accessible only to users with role VETERINARY."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered users retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)")
    })
    @GetMapping("/filter")
    public ResponseEntity<List<UserResponse>> getFilterUserByRole(
            @RequestParam(required = false) Role role,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        List<UserResponse> filteredUsers = userService.getFilterUserByRole(role, userDetail);
        return ResponseEntity.ok(filteredUsers);
    }

    @Operation(
            summary = "Create user",
            description = "Creates a new user. Only VETERINARY role can create users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error in request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., username or email already exists)")
    })
    @PostMapping()
    public ResponseEntity<UserResponse> createUser(
            @RequestBody @Valid UserRequest userRequest,
            @AuthenticationPrincipal UserDetail userDetail) {
        UserResponse userResponse = userService.createUser(userRequest, userDetail);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update user",
            description = "Updates user data by ID. Only VETERINARY role can update users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error in request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @RequestBody @Valid UserUpdateRequest userUpdateRequest,
                                                   @AuthenticationPrincipal UserDetail userDetail) {
        UserResponse updatedUser = userService.updateUser(id, userUpdateRequest, userDetail);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by ID and returns a confirmation message. Only VETERINARY role can delete users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized (missing/invalid JWT)"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserDetail userDetail) {
        Map<String, String> response = userService.deleteUser(id, userDetail);
        return ResponseEntity.ok(response);
    }
}
