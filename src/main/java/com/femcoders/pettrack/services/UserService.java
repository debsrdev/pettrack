package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.user.UserMapper;
import com.femcoders.pettrack.dtos.user.UserRequest;
import com.femcoders.pettrack.dtos.user.UserResponse;
import com.femcoders.pettrack.exceptions.EntityNotFoundException;
import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.repositories.UserRepository;
import com.femcoders.pettrack.security.UserDetail;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetail loadUserByUsername(String identifier) throws EntityNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(identifier)
                .or(()-> userRepository.findByEmailIgnoreCase(identifier))
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), " username or email " + identifier));
        return new UserDetail(user);
    }

    @Transactional
    public UserResponse registerUser(UserRequest userRequestDTO){
        if (userRepository.existsByUsername(userRequestDTO.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(userRequestDTO.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        Role defaultRole = Role.USER;

        User user = userMapper.dtoToEntity(userRequestDTO, defaultRole);

        user.setPassword(bCryptPasswordEncoder.encode(userRequestDTO.password()));

        userRepository.save(user);
        return userMapper.entityToDto(user);
    }
}
