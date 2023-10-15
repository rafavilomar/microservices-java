package com.security.module.user;

import com.security.error.exception.ConflictException;
import com.security.module.role.IRoleService;
import com.security.module.role.entites.Role;
import com.security.module.user.User;
import com.security.module.user.UserRepository;
import com.security.module.user.UserService;
import com.security.module.user.dto.NewUserDto;
import com.security.module.user.dto.UserResponseDto;
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
    @Mock
    private IRoleService roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldBeOk() {
        long expectedResponse = 1;
        NewUserDto newUser = new NewUserDto("test@gmail.com", "fullName", "password", 1L);

        when(passwordEncoder.encode(newUser.password())).thenReturn("password encoded");
        when(roleService.findByIdEntity(newUser.idRole())).thenReturn(Role.builder().build());

        when(repository.save(any(User.class))).thenReturn(User.builder().id(expectedResponse).build());

        long actualResponse = underTest.create(newUser);

        assertEquals(expectedResponse, actualResponse);

        verify(repository, times(1)).existsByEmail(newUser.email());
        verify(repository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(newUser.password());
        verify(roleService, times(1)).findByIdEntity(newUser.idRole());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void create_DuplicatedEmail() {
        NewUserDto newUser = new NewUserDto("test@gmail.com", "fullName", "password", 1L);

        when(repository.existsByEmail(newUser.email())).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> underTest.create(newUser));

        assertEquals("There is another account with this email: " + newUser.email(), exception.getMessage());

        verify(repository, times(1)).existsByEmail(newUser.email());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(passwordEncoder, roleService);
    }

    @Test
    void findByEmail_ShouldBeOk() {
        String email = "test@gmail.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .fullName("fullName")
                .role(Role.builder()
                        .id(1L)
                        .name("role")
                        .build())
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