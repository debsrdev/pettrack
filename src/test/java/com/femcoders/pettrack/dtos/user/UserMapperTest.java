package com.femcoders.pettrack.dtos.user;

import com.femcoders.pettrack.dtos.user.UserMapperImpl;
import com.femcoders.pettrack.dtos.user.UserRequest;
import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {
    private final UserMapperImpl mapper = new UserMapperImpl();
    private User userNewWithRole;
    private UserRequest userNewWithoutRole;

    @BeforeEach
    void setup() {
        userNewWithRole = User.builder()
                .id(1L)
                .username("Debora")
                .email("debora@user.com")
                .role(Role.USER)
                .build();
        userNewWithoutRole = new UserRequest("Debora","debora@user.com", "Debora123.");
    }

    @Test
    void entityToDto_ok() {
        var dto = mapper.entityToDto(userNewWithRole);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.username()).isEqualTo("Debora");
        assertThat(dto.role()).isEqualTo("USER");
    }

    @Test
    void dtoToEntity_ok() {
        var user = mapper.dtoToEntity(userNewWithoutRole, Role.USER);
        assertThat(user.getUsername()).isEqualTo("Debora");
        assertThat(user.getEmail()).isEqualTo("debora@user.com");
    }
}
