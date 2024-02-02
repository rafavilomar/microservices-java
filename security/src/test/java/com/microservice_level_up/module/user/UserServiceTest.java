package com.microservice_level_up.module.user;

import com.microservice_level_up.error.http_exeption.BadRequestException;
import com.microservice_level_up.error.http_exeption.InternalErrorException;
import com.microservice_level_up.kafka.events.Event;
import com.microservice_level_up.kafka.events.EventType;
import com.microservice_level_up.module.role.IRoleService;
import com.microservice_level_up.module.role.entity.Role;
import com.microservice_level_up.module.user.dto.RegisterCustomerRequest;
import com.microservice_level_up.module.user.dto.RegisterUserRequest;
import com.microservice_level_up.notification.CustomerCreatedNotification;
import common.grpc.common.CustomerRegistrationRequest;
import common.grpc.common.CustomerServiceGrpc;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Mock
    private CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub;
    @Mock
    private KafkaTemplate<String, Event<?>> producer;

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

        underTest.registerCustomer(newUser);

        verify(passwordEncoder, times(1)).encode(newUser.password());
        verify(userRepository, times(1)).existsByEmail(newUser.email());
        verify(roleService, times(1)).findByName(roleName);
        verify(userRepository, times(1)).save(any(User.class));
        verify(customerServiceBlockingStub, times(1)).register(CustomerRegistrationRequest.newBuilder()
                .setFirstName(newUser.firstName())
                .setLastName(newUser.lastName())
                .setEmail(newUser.email())
                .setIdUser(user.getId())
                .setAddress(newUser.address())
                .setCountry(newUser.country())
                .build());
        verify(producer, times(1)).send(eq("customer_created"), any(Event.class));
        verifyNoMoreInteractions(userRepository, roleService, passwordEncoder, customerServiceBlockingStub, producer);
    }

    @Test
    void registerUser_DuplicatedEmail(){
        RegisterUserRequest newUser = RegisterUserRequest.builder()
                .email("david@gmail.com")
                .password("PASS")
                .roleName("Admin")
                .build();

        when(userRepository.existsByEmail(newUser.email())).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> underTest.registerUser(newUser));

        assertEquals("Seems this email is already registered. Try login!", exception.getMessage());

        verify(userRepository, times(1)).existsByEmail(newUser.email());
        verifyNoMoreInteractions(userRepository, roleService);
    }

    @Test
    void registerUser_RoleNotFound(){
        RegisterUserRequest newUser = RegisterUserRequest.builder()
                .email("david@gmail.com")
                .password("PASS")
                .roleName("Admin")
                .build();

        when(roleService.findByName(newUser.roleName())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> underTest.registerUser(newUser));

        verify(userRepository, times(1)).existsByEmail(newUser.email());
        verify(roleService, times(1)).findByName(newUser.roleName());
        verifyNoMoreInteractions(userRepository, roleService);

    }

    @Test
    void registerUser_ShouldBeOk() {
        String passwordEncoded = "PASS_ENCODED";
        RegisterUserRequest newUser = RegisterUserRequest.builder()
                .email("david@gmail.com")
                .password("PASS")
                .roleName("Admin")
                .build();

        Role customerRole = Role.builder().id(1L).name(newUser.roleName()).build();
        User user = User.builder()
                .id(1L)
                .email(newUser.email())
                .password(passwordEncoded)
                .role(customerRole)
                .build();

        when(passwordEncoder.encode(newUser.password())).thenReturn(passwordEncoded);
        when(roleService.findByName(newUser.roleName())).thenReturn(customerRole);
        when(userRepository.save(any(User.class))).thenReturn(user);

        underTest.registerUser(newUser);

        verify(passwordEncoder, times(1)).encode(newUser.password());
        verify(userRepository, times(1)).existsByEmail(newUser.email());
        verify(roleService, times(1)).findByName(newUser.roleName());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository, roleService, passwordEncoder);
        verifyNoInteractions(producer, customerServiceBlockingStub);
    }
}