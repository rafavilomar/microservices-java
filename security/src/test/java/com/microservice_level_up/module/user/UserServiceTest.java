package com.microservice_level_up.module.user;

import com.microservice_level_up.error.duplicated_user_email.DuplicatedUserEmailException;
import com.microservice_level_up.module.user.dto.NewUserDto;
import com.microservice_level_up.module.user.dto.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService underTest;

    @Mock
    private UserRepository repository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldBeOk() {
        NewUserDto newUser = new NewUserDto("test@gmail.com", "fullName", "password");

        when(passwordEncoder.encode(newUser.password())).thenReturn("password encoded");

        underTest.create(newUser);

        verify(repository, times(1)).existsByEmail(newUser.email());
        verify(repository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(newUser.password());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void create_DuplicatedEmail() {
        NewUserDto newUser = new NewUserDto("test@gmail.com", "fullName", "password");

        when(repository.existsByEmail(newUser.email())).thenReturn(true);

        DuplicatedUserEmailException exception = assertThrows(
                DuplicatedUserEmailException.class,
                () -> underTest.create(newUser));

        assertEquals("There is another account with this email: " + newUser.email(), exception.getMessage());

        verify(repository, times(1)).existsByEmail(newUser.email());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void findByEmail_ShouldBeOk() {
        String email = "test@gmail.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .fullName("fullName")
                .build();

        when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        UserResponseDto actualResponse = underTest.findByEmail(email);

        assertAll(
                "Find user by email",
                () -> assertEquals(user.getId(), actualResponse.id()),
                () -> assertEquals(user.getEmail(), actualResponse.email()),
                () -> assertEquals(user.getFullName(), actualResponse.fullName())
        );

        verify(repository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(passwordEncoder);
    }
}