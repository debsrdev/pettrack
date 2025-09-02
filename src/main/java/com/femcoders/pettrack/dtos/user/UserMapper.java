package com.femcoders.pettrack.dtos.user;

import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;

public interface UserMapper {
    User dtoToEntity (UserRequest userRequest, Role role);
    UserResponse entityToDto (User user);
}
