package com.femcoders.pettrack.utils;

import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.security.UserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("Role Validator Tests")
public class RoleValidatorTest {
    private UserDetail userDetailVet;
    private UserDetail userDetailUser;

    @BeforeEach
    void setup() {
        userDetailVet = new UserDetail(
                User.builder()
                        .id(900L)
                        .username("VetTest")
                        .role(Role.VETERINARY)
                        .build()
        );
        userDetailUser = new UserDetail(
                User.builder()
                        .id(901L)
                        .username("UserTest")
                        .role(Role.USER)
                        .build()
        );
    }

    @Test
    @DisplayName("Should return true when veterinary")
    void returnsTrue_whenVeterinary() {
        assertThat(RoleValidator.isVeterinary(userDetailVet)).isTrue();
    }

    @Test
    @DisplayName("Should return exception when is not veterinary")
    void returnsException_whenIsNotVeterinary() {
        assertThatThrownBy(()->RoleValidator.validateVeterinary(userDetailUser, "Only veterinaries can manage users"))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Only veterinaries can manage users");
    }
}
