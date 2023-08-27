package com.microservice_level_up.module.user;

import com.microservice_level_up.error.conflict.ConflictException;
import com.microservice_level_up.module.role.IRoleService;
import com.microservice_level_up.module.user.dto.NewUserDto;
import com.microservice_level_up.module.user.dto.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Slf4j
@Service
public record UserService(
        UserRepository repository,
        BCryptPasswordEncoder passwordEncoder,
        IRoleService roleService) implements IUserService {
    @Override
    public void create(NewUserDto newUser) {
        log.info("Create new role user {}", newUser.email());
        if (repository.existsByEmail(newUser.email()))
            throw new ConflictException("There is another account with this email: " + newUser.email());

        repository.save(User.builder()
                .email(newUser.email())
                .fullName(newUser.fullName())
                .password(passwordEncoder().encode(newUser.password()))
                .role(roleService.findByIdEntity(newUser.idRole()))
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    public UserResponseDto findByEmail(String email) {
        log.info("Get user by email {}", email);
        return repository.findByEmail(email)
                .map(this::mapResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
    }

    private UserResponseDto mapResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleName(user.getRole().getName())
                .idRole(user.getRole().getId())
                .build();
    }
}
