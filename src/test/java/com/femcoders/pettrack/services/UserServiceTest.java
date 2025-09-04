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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    private User vetEntity;
    private User regularEntity;
    private UserDetail vetPrincipal;
    private UserDetail regularPrincipal;

    private UserRequest createReq;
    private UserResponse userResp1;
    private UserResponse userResp2;

    @BeforeEach
    void setup() {
        vetEntity = User.builder()
                .id(10L)
                .username("Vet")
                .email("vet@vet.com")
                .role(Role.VETERINARY).build();
        regularEntity = User.builder()
                .id(1L)
                .username("Debora")
                .email("debora@user.com")
                .role(Role.USER)
                .build();
        vetPrincipal = new UserDetail(vetEntity);
        regularPrincipal = new UserDetail(regularEntity);

        createReq = new UserRequest(
                "NuevoUser",
                "nuevo@user.com",
                "Passw0rd.");
        userResp1 = new UserResponse(
                1L,
                "Debora",
                "debora@user.com",
                "USER");
        userResp2 = new UserResponse(
                2L,
                "Carmen",
                "carmen@user.com",
                "VETERINARY");
    }

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsersTests {
        @Test
        @DisplayName("Should return list when principal is veterinary")
        void shouldReturnList_whenIsVet() {
            given(userRepository.findAll()).willReturn(List.of(regularEntity, vetEntity));
            given(userMapper.entityToDto(regularEntity)).willReturn(userResp1);
            given(userMapper.entityToDto(vetEntity)).willReturn(userResp2);

            var result = userService.getAllUsers(vetPrincipal);

            assertThat(result).containsExactly(userResp1, userResp2);

            verify(userRepository).findAll();
            verify(userMapper).entityToDto(regularEntity);
            verify(userMapper).entityToDto(vetEntity);
        }

        @Test
        @DisplayName("Should throw SecurityException when principal is not veterinary")
        void shouldThrowSecurityException_whenIsNotVet() {
            var thrown = catchThrowable(() -> userService.getAllUsers(regularPrincipal));

            assertThat(thrown)
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("Only veterinarians can view all users");
        }
    }

    @Nested
    @DisplayName("getUserById()")
    class GetByIdTests {
        @Test
        @DisplayName("Should return user when id exists and principal is vet")
        void shouldReturnUser_whenIdExistsAndIsVet() {
            given(userRepository.findById(1L)).willReturn(Optional.of(regularEntity));
            given(userMapper.entityToDto(regularEntity)).willReturn(userResp1);

            var res = userService.getUserById(1L, vetPrincipal);

            assertThat(res.username()).isEqualTo("Debora");
            verify(userRepository).findById(1L);
            verify(userMapper).entityToDto(regularEntity);
        }

        @Test
        @DisplayName("Should throw 404 when id does not exist")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            var thrown = catchThrowable(() -> userService.getUserById(999L, vetPrincipal));

            assertThat(thrown)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User not found with id 999");

            verify(userRepository).findById(999L);
        }

        @Test
        @DisplayName("Should return user even when principal is not vet")
        void shouldReturnUser_whenIsNotVet() {
            given(userRepository.findById(1L)).willReturn(Optional.of(regularEntity));
            given(userMapper.entityToDto(regularEntity)).willReturn(userResp1);

            var res = userService.getUserById(1L, regularPrincipal);

            assertThat(res).isNotNull();
            assertThat(res.username()).isEqualTo("Debora");

            verify(userRepository).findById(1L);
            verify(userMapper).entityToDto(regularEntity);
        }
    }

    @Nested
    @DisplayName("createUser()")
    class CreateUserTests {
        @Test
        @DisplayName("Should create user when vet")
        void shouldCreateUser_whenIsVet() {
            var toSave = User.builder()
                    .username("NuevoUser")
                    .email("nuevo@user.com")
                    .password("...")
                    .role(Role.USER)
                    .build();

            var saved = User.builder()
                    .id(50L)
                    .username("NuevoUser")
                    .email("nuevo@user.com")
                    .role(Role.USER)
                    .build();

            given(userMapper.dtoToEntity(createReq, Role.USER)).willReturn(toSave);
            given(userRepository.save(toSave)).willReturn(saved);
            given(userMapper.entityToDto(any(User.class)))
                    .willReturn(new UserResponse(50L, "NuevoUser", "nuevo@user.com", "USER"));

            var res = userService.createUser(createReq, vetPrincipal);

            assertThat(res.id()).isEqualTo(50L);
            assertThat(res.username()).isEqualTo("NuevoUser");

            verify(userMapper).dtoToEntity(createReq, Role.USER);
            verify(userRepository).save(toSave);
            verify(userMapper).entityToDto(any(User.class));
        }

        @Test
        @DisplayName("Should throw SecurityException when not vet")
        void shouldThrowSecurityException_whenIsNotVet() {
            var thrown = catchThrowable(() -> userService.createUser(createReq, regularPrincipal));

            assertThat(thrown)
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("Only veterinarians can create users");
        }
    }

    @Nested
    @DisplayName("updateUser()")
    class UpdateUserTests {
        @Test
        @DisplayName("Should update when id exists and vet")
        void shouldUpdateUser_whenIdExistsAndIsVet() {
            var updReq = new UserUpdateRequest(
                    "DeboraUpdated",
                    "debora.updated@user.com",
                    "Debora123.",
                    Role.USER
            );

            given(userRepository.findById(1L)).willReturn(Optional.of(regularEntity));
            given(bCryptPasswordEncoder.encode(any(CharSequence.class))).willReturn("hashed-password");
            given(userRepository.save(regularEntity)).willReturn(regularEntity);
            given(userMapper.entityToDto(regularEntity))
                    .willReturn(new UserResponse(
                            1L,
                            "DeboraUpdated",
                            "debora.updated@user.com",
                            "USER"
                    ));

            var res = userService.updateUser(1L, updReq, vetPrincipal);

            assertThat(res.username()).isEqualTo("DeboraUpdated");
            assertThat(res.email()).isEqualTo("debora.updated@user.com");

            verify(userRepository).findById(1L);
            verify(bCryptPasswordEncoder).encode("Debora123.");
            verify(userRepository).save(regularEntity);
            verify(userMapper).entityToDto(regularEntity);
        }

        @Test
        @DisplayName("Should throw 404 when id not found")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            var updReq = new UserUpdateRequest("x", "x@x.com", "Passw0rd.", Role.USER);

            var thrown = catchThrowable(() -> userService.updateUser(999L, updReq, vetPrincipal));

            assertThat(thrown)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User not found with id 999");

            verify(userRepository).findById(999L);
        }

        @Test
        @DisplayName("Should throw SecurityException when not vet")
        void shouldThrowSecurityException_whenIsNotVet() {
            var updReq = new UserUpdateRequest("x", "x@x.com", "Passw0rd.", Role.USER);

            var thrown = catchThrowable(() ->
                    userService.updateUser(1L, updReq, regularPrincipal)
            );

            assertThat(thrown)
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("Only veterinarians can edit users");
        }
    }


    @Nested
    @DisplayName("deleteUser()")
    class DeleteUserTests {
        @Test
        @DisplayName("Should delete when vet and id exists")
        void shouldDeleteUser_whenIsVetAndIdExists() {
            given(userRepository.findById(1L)).willReturn(Optional.of(regularEntity));

            var res = userService.deleteUser(1L, vetPrincipal);

            assertThat(res.get("message")).contains("has been deleted");
            verify(userRepository).findById(1L);
            verify(userRepository).delete(regularEntity);
        }

        @Test
        @DisplayName("Should throw 404 when id not found")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            var thrown = catchThrowable(() -> userService.deleteUser(999L, vetPrincipal));

            assertThat(thrown)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("User not found with id 999");

            verify(userRepository).findById(999L);
        }

        @Test
        @DisplayName("Should throw SecurityException when not vet")
        void shouldThrowSecurityException_whenIsNotVet() {
            var thrown = catchThrowable(() -> userService.deleteUser(1L, regularPrincipal));

            assertThat(thrown)
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("Only veterinarians can delete users");
        }
    }

    @Nested
    @DisplayName("getFilterUserByRole()")
    class FilterByRoleTests {
        @Test
        @DisplayName("Should return only VETERINARY when vet principal")
        void shouldReturnVeterinaries_whenIsVet() {
            given(userRepository.findAll(any(Specification.class))).willReturn(List.of(vetEntity));
            given(userMapper.entityToDto(vetEntity)).willReturn(userResp2);

            var res = userService.getFilterUserByRole(Role.VETERINARY, vetPrincipal);

            assertThat(res).containsExactly(userResp2);

            verify(userRepository).findAll(any(Specification.class));
            verify(userMapper).entityToDto(vetEntity);
        }

        @Test
        @DisplayName("Should throw SecurityException when not vet")
        void shouldThrowSecurityException_whenIsNotVet() {
            var thrown = catchThrowable(() -> userService.getFilterUserByRole(Role.VETERINARY, regularPrincipal));

            assertThat(thrown)
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("Only veterinarians can filter users");
        }
    }
}
