package com.femcoders.pettrack.dtos.user;

import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public User dtoToEntity(UserRequest userRequestDTO, Role role) {
        return User.builder()
                .username(userRequestDTO.username())
                .email(userRequestDTO.email())
                .password(userRequestDTO.password())
                .role(role)
                .build();
    }

    @Override
    public UserResponse entityToDto(User user){
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
