package com.femcoders.pettrack.utils;

import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.security.UserDetail;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleValidator {
    public static void validateVeterinary(UserDetail userDetail, String errorMessage) {
        if (userDetail == null || userDetail.getUsername() == null) {
            throw new IllegalArgumentException("User information is missing or invalid");
        }
        if (!userDetail.getRole().equals(Role.VETERINARY.name())) {
            throw new SecurityException(errorMessage);
        }
    }
    public static boolean isVeterinary(UserDetail userDetail) {
        return userDetail != null && userDetail.getRole().equals(Role.VETERINARY.name());
    }
}
