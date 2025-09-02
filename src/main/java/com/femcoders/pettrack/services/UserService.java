package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordRequest;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.dtos.user.UserRequest;
import com.femcoders.pettrack.dtos.user.UserResponse;
import com.femcoders.pettrack.security.UserDetail;

import java.util.List;

public interface UserService {
    UserDetail loadUserByUsername(String identifier);
    UserResponse registerUser(UserRequest userRequestDTO);
    List<UserResponse> getAllUsers(UserDetail userDetail);
    UserResponse getUserById(Long id, UserDetail userDetail);
    UserResponse createUser(UserRequest UserRequest, UserDetail userDetail);

}
