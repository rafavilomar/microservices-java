package com.microservice_level_up.module.auth;

import com.microservice_level_up.error.http_exeption.UnauthorizedException;
import com.microservice_level_up.module.auth.dtos.LoginResponseDTO;
import com.microservice_level_up.module.auth.dtos.TokensResponseDTO;
import com.microservice_level_up.module.role.entity.Role;
import com.microservice_level_up.module.user.IUserService;
import com.microservice_level_up.module.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @InjectMocks
    private AuthService underTest;

    @Mock
    private IUserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(underTest, "jwtSecretKey", "TEST_VALUE");
    }

    private User getUser(String email) {
        return User.builder()
                .id(1L)
                .email(email)
                .password("password")
                .active(true)
                .role(Role.builder()
                        .id(1L)
                        .name("Role")
                        .permissions(new ArrayList<>())
                        .active(true)
                        .build())
                .build();
    }

    @Test
    void loadUserByUsername_ShouldBeOk() {
        String email = "user@gmail.com";
        User user = getUser(email);

        when(userService.getByEmail(email)).thenReturn(user);

        UserDetails actualResponse = underTest.loadUserByUsername(email);

        assertAll(
                "Load user for authentication",
                () -> assertNotNull(actualResponse),
                () -> assertEquals(email, actualResponse.getUsername()),
                () -> assertEquals(user.getPassword(), actualResponse.getPassword()),
                () -> assertEquals(0, actualResponse.getAuthorities().size())
        );

        verify(userService, times(1)).getByEmail(email);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void generateAccessToken_ShouldBeOk() {
        String email = "user@gmail.com";

        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
                email,
                "password",
                new ArrayList<>()
        );

        String accessToken = underTest.generateAccessToken(user);
        assertNotNull(accessToken);

        String tokenEmail = underTest.getEmail(accessToken);
        assertEquals(email, tokenEmail);

        verifyNoInteractions(userService);
    }

    @Test
    void handleRefreshToken_ShouldBeOk() {
        String email = "user@gmail.com";
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
                email,
                "password",
                new ArrayList<>()
        );

        when(userService.getByEmail(email)).thenReturn(getUser(email));

        String refreshToken = underTest.generateRefreshToken(user);
        assertNotNull(refreshToken);

        TokensResponseDTO tokensResponse = underTest.refreshToken("Bearer " + refreshToken);

        assertNotNull(tokensResponse);

        assertAll(
                "Handle refresh token should be ok",
                () -> assertEquals(refreshToken, tokensResponse.refreshToken()),
                () -> assertNotEquals(refreshToken, tokensResponse.accessToken())
        );

        verify(userService, times(1)).getByEmail(email);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getPermissions_ShouldBeOk() {
        String accessToken = underTest.generateAccessToken(getUser("user@gmail.com"));

        List<SimpleGrantedAuthority> actualResponse = underTest.getPermissions(accessToken);

        assertEquals(0, actualResponse.size());

        verifyNoInteractions(userService);
    }


    @Test
    void refreshToken_TokenNotProvided() {
        assertThrows(UnauthorizedException.class, () -> underTest.refreshToken(null));
        verifyNoInteractions( userService);
    }

    @Test
    void refreshToken_InvalidToken() {
        assertThrows(UnauthorizedException.class, () -> underTest.refreshToken("Bearer token"));
        verifyNoInteractions(userService);
    }

    @Test
    void login() {
        String email = "user@gmail.com";
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
                email,
                "password",
                new ArrayList<>()
        );
        User userFromDb = getUser(email);

        when(userService.getByEmail(email)).thenReturn(userFromDb);

        LoginResponseDTO actualResponse = underTest.login(user);

        assertNotNull(actualResponse);
        assertAll(
                "Login",
                () -> assertNotNull(actualResponse.accessToken()),
                () -> assertNotNull(actualResponse.refreshToken()),
                () -> assertEquals(userFromDb.getEmail(), actualResponse.email()),
                () -> assertEquals(userFromDb.getRole().getName(), actualResponse.roleName())
        );

        verify(userService, times(1)).getByEmail(email);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void login_InactiveUser() {
        String email = "user@gmail.com";
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
                email,
                "password",
                new ArrayList<>()
        );
        User userFromDb = getUser(email);
        userFromDb.setActive(false);

        when(userService.getByEmail(email)).thenReturn(userFromDb);

        assertThrows(UnauthorizedException.class, () -> underTest.login(user));

        verify(userService, times(1)).getByEmail(email);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void login_InactiveRole() {
        String email = "user@gmail.com";
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
                email,
                "password",
                new ArrayList<>()
        );
        User userFromDb = getUser(email);
        userFromDb.getRole().setActive(false);

        when(userService.getByEmail(email)).thenReturn(userFromDb);

        assertThrows(UnauthorizedException.class, () -> underTest.login(user));

        verify(userService, times(1)).getByEmail(email);
        verifyNoMoreInteractions(userService);
    }
}