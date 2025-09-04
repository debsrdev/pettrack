package com.femcoders.pettrack.security.jwt;

import com.femcoders.pettrack.models.Role;
import com.femcoders.pettrack.models.User;
import com.femcoders.pettrack.security.UserDetail;
import com.femcoders.pettrack.services.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("JwtAuthFilter - valid token path")
public class JwtAuthFilterValidTokenTest {

    @AfterEach
    void teardown() { SecurityContextHolder.clearContext(); }

    @Test
    @DisplayName("Should authenticate and set SecurityContext when token is valid")
    void shouldAuthenticate_whenTokenValid() throws Exception {
        var jwtService = mock(JwtService.class);
        var userService = mock(UserServiceImpl.class);
        var filter = new JwtAuthFilter(jwtService, userService);

        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        given(req.getHeader("Authorization")).willReturn("Bearer valid-token");
        given(jwtService.isValidToken("valid-token")).willReturn(true);
        given(jwtService.extractUsername("valid-token")).willReturn("Debora");

        var user = User.builder().id(1L).username("Debora").role(Role.USER).build();
        var ud = new UserDetail(user);
        given(userService.loadUserByUsername("Debora")).willReturn(ud);

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("Debora");
    }
}