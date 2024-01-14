package com.microservice_level_up.module.user;

import com.microservice_level_up.error.http_exeption.BadRequestException;
import com.microservice_level_up.error.http_exeption.InternalErrorException;
import com.microservice_level_up.module.role.IRoleService;
import com.microservice_level_up.module.role.RoleService;
import com.microservice_level_up.module.role.entity.Role;
import com.microservice_level_up.module.role.repository.RoleRepository;
import com.microservice_level_up.module.user.dto.RegisterCustomerRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService underTest;

    @Mock
    private UserRepository userRepository;
    @Mock
    private IRoleService roleService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerCustomer_ShouldBeOk() {
        String roleName = "Customer";
        String passwordEncoded = "PASS_ENCODED";
        RegisterCustomerRequest newUser = RegisterCustomerRequest.builder()
                .email("david@gmail.com")
                .password("PASS")
                .firstName("David")
                .lastName("Dunk")
                .country("Canada")
                .address("Address")
                .build();

        Role customerRole = Role.builder().id(1L).name(roleName).build();
        User user = User.builder()
                .id(1L)
                .email(newUser.email())
                .password(passwordEncoded)
                .role(customerRole)
                .build();

        when(passwordEncoder.encode(newUser.password())).thenReturn(passwordEncoded);
        when(roleService.findByName(roleName)).thenReturn(customerRole);
        when(userRepository.save(any(User.class))).thenReturn(user);
        //todo: add customer and email notification calls

        underTest.registerCustomer(newUser);

        verify(passwordEncoder, times(1)).encode(newUser.password());
        verify(userRepository, times(1)).existsByEmail(newUser.email());
        verify(roleService, times(1)).findByName(roleName);
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository, roleService, passwordEncoder);
    }

    @Test
    void registerCustomer_DuplicatedEmail(){
        RegisterCustomerRequest newUser = RegisterCustomerRequest.builder()
                .email("david@gmail.com")
                .password("PASS")
                .firstName("David")
                .lastName("Dunk")
                .country("Canada")
                .address("Address")
                .build();

        when(userRepository.existsByEmail(newUser.email())).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> underTest.registerCustomer(newUser));

        assertEquals("Seems this email is already registered. Try login!", exception.getMessage());

        verify(userRepository, times(1)).existsByEmail(newUser.email());
        verifyNoMoreInteractions(userRepository, roleService);
    }

    @Test
    void registerCustomer_CustomerRoleNotFound(){
        String roleName = "Customer";
        RegisterCustomerRequest newUser = RegisterCustomerRequest.builder()
                .email("david@gmail.com")
                .password("PASS")
                .firstName("David")
                .lastName("Dunk")
                .country("Canada")
                .address("Address")
                .build();

        when(roleService.findByName(roleName)).thenThrow(EntityNotFoundException.class);

        InternalErrorException exception = assertThrows(
                InternalErrorException.class,
                () -> underTest.registerCustomer(newUser));

        assertEquals("Can't register in this moment. Please try later", exception.getMessage());

        verify(userRepository, times(1)).existsByEmail(newUser.email());
        verify(roleService, times(1)).findByName(roleName);
        verifyNoMoreInteractions(userRepository, roleService);

    }
}