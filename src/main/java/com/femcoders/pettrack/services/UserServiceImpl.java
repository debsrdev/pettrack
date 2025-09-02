package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.user.UserMapper;
import com.femcoders.pettrack.dtos.user.UserRequest;
import com.femcoders.pettrack.dtos.user.UserResponse;
import com.femcoders.pettrack.dtos.user.UserUpdateRequest;
import com.femcoders.pettrack.exceptions.EntityNotFoundException;
import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.repositories.UserRepository;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.utils.RoleValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
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

    public List<UserResponse> getAllUsers(UserDetail userDetail) {
        RoleValidator.validateVeterinary(userDetail, "Only veterinarians can view all users");

        List<User> userList = userRepository.findAll();

        return userList.stream()
                .map(user->userMapper.entityToDto(user))
                .toList();
    }

    public UserResponse getUserById(Long userId, UserDetail userDetail) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new EntityNotFoundException(User.class.getSimpleName(), userId));

        if (RoleValidator.isVeterinary(userDetail)) {
            return userMapper.entityToDto(user);
        }

        if (userDetail.getId().equals(userId)) {
            return userMapper.entityToDto(user);
        }

        throw new SecurityException("You do not have permission to view this user");
    }

    public List<UserResponse> getFilterUserByRole(Role role, UserDetail userDetail) {
        RoleValidator.validateVeterinary(userDetail, "Only veterinarians can filter users");

        List<User> users = userRepository.findAll(
                (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    boolean hasRole = role != null;

                    if (hasRole) {
                        predicates.add(cb.equal(root.get("role"), role));
                    }
                    return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                });
        return users.stream()
                .map(user -> userMapper.entityToDto(user))
                .toList();
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest, UserDetail userDetail){
        RoleValidator.validateVeterinary(userDetail, "Only veterinarians can create users");

        if (userRepository.existsByUsername(userRequest.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(userRequest.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = userMapper.dtoToEntity(userRequest, Role.USER);
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.password()));

        userRepository.save(user);

        return userMapper.entityToDto(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest, UserDetail userDetail){
        RoleValidator.validateVeterinary(userDetail, "Only veterinarians can edit users");

        User user = userRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(User.class.getSimpleName(), id));

        user.setUsername(userUpdateRequest.username());
        user.setEmail(userUpdateRequest.email());
        user.setPassword(bCryptPasswordEncoder.encode(userUpdateRequest.password()));
        user.setRole(userUpdateRequest.role() != null ? userUpdateRequest.role() : Role.USER);

        userRepository.save(user);
        return userMapper.entityToDto(user);
    }

    @Transactional
    public Map<String, String> deleteUser(Long id, UserDetail userDetail){
        RoleValidator.validateVeterinary(userDetail, "Only veterinarians can delete users");

        User userToDelete = userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), id));

        userRepository.delete(userToDelete);

        String message = "User with id: " + userToDelete.getId() + " has been deleted successfully";
        return Map.of("message", message);
    }
}
