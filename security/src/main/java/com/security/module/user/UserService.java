package com.security.module.user;

import com.security.error.exception.ConflictException;
import com.security.error.exception.UnauthorizedException;
import com.security.module.role.IRoleService;
import com.security.module.user.dto.NewUserDto;
import com.security.module.user.dto.UserResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public record UserService(
        UserRepository repository,
        BCryptPasswordEncoder passwordEncoder,
        IRoleService roleService) implements IUserService {

    @Override
    public long create(NewUserDto newUser) {
        log.info("Create new role user {}", newUser.email());
        if (repository.existsByEmail(newUser.email()))
            throw new ConflictException("There is another account with this email: " + newUser.email());

        return repository.save(User.builder()
                .email(newUser.email())
                .fullName(newUser.fullName())
                .password(passwordEncoder().encode(newUser.password()))
                .role(roleService.findByIdEntity(newUser.idRole()))
                .active(true)
                .createdAt(LocalDateTime.now())
                .build()).getId();
    }

    @Override
    public UserResponseDto findByEmail(String email) {
        log.info("Get user by email {}", email);
        return mapResponse(findEntityByEmail(email));
    }

    @Override
    public User findEntityByEmail(String email) {
        log.info("Get user by email {}", email);
        return repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getLoggedUser() {
        log.info("Get logged user");
        String loggedUserEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        if (loggedUserEmail == null) {
            throw new UnauthorizedException("Authorization required");
        }
        return findEntityByEmail(loggedUserEmail);
    }

    @Override
    public UserResponseDto getLoggedUserResponse() {
        return mapResponse(getLoggedUser());
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
