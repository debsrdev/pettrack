package com.femcoders.pettrack.security.jwt;

import com.femcoders.pettrack.services.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("JwtAuthFilter Unit Tests")
public class JwtAuthFilterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should pass through when no Authorization header")
    void shouldPass_whenNoAuthorizationHeader() throws Exception {
        var jwtService = mock(JwtService.class);
        var userService = mock(UserServiceImpl.class);
        var filter = new JwtAuthFilter(jwtService, userService);


        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        given(req.getHeader("Authorization")).willReturn(null);

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should pass through when header without Bearer prefix")
    void shouldPass_whenHeaderWithoutBearer() throws Exception {
        var jwtService = mock(JwtService.class);
        var userService = mock(UserServiceImpl.class);
        var filter = new JwtAuthFilter(jwtService, userService);


        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        given(req.getHeader("Authorization")).willReturn("Token abc");

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should not authenticate when token invalid")
    void shouldNotAuthenticate_whenTokenInvalid() throws Exception {
        var jwtService = mock(JwtService.class);
        var userService = mock(UserServiceImpl.class);
        var filter = new JwtAuthFilter(jwtService, userService);


        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        given(req.getHeader("Authorization")).willReturn("Bearer invalid");
        given(jwtService.isValidToken("invalid")).willReturn(false);

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        verify(jwtService).isValidToken("invalid");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
