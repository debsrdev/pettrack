package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordRequest;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.dtos.user.UserRequest;
import com.femcoders.pettrack.dtos.user.UserResponse;
import com.femcoders.pettrack.dtos.user.UserUpdateRequest;
import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.security.UserDetail;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDetail loadUserByUsername(String identifier);
    UserResponse registerUser(UserRequest userRequestDTO);
    List<UserResponse> getAllUsers(UserDetail userDetail);
    UserResponse getUserById(Long id, UserDetail userDetail);
    List<UserResponse> getFilterUserByRole(Role role, UserDetail userDetail);
    UserResponse createUser(UserRequest userRequest, UserDetail userDetail);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest, UserDetail userDetail);
    Map<String, String> deleteUser(Long id, UserDetail userDetail);
}
