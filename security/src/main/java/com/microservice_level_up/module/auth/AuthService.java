package com.microservice_level_up.module.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.microservice_level_up.auth.TokenValidationService;
import com.microservice_level_up.error.http_exeption.UnauthorizedException;
import com.microservice_level_up.module.auth.dtos.LoginResponseDTO;
import com.microservice_level_up.module.auth.dtos.TokensResponseDTO;
import com.microservice_level_up.module.role.entity.Permission;
import com.microservice_level_up.module.user.IUserService;
import com.microservice_level_up.module.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService, IAuthService {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    private final IUserService userService;

    /** 10 minutes */
    private static final int ACCESS_TOKEN_EXPIRATION = 10 * 60 * 1000;
    /** 12 hours */
    private static final int REFRESH_TOKEN_EXPIRATION = 12 * 60 * 60 * 1000;

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("User {} is trying to access the system", email);
        return mapUserDetails(userService.getByEmail(email));
    }

    private org.springframework.security.core.userdetails.User mapUserDetails(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRole()
                .getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokensResponseDTO refreshToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Incorrect refresh token {}", authorizationHeader);
            throw new UnauthorizedException("Provide a valid refresh token");
        }

        try {
            String refreshToken = authorizationHeader.substring("Bearer ".length());

            String email = TokenValidationService.getEmail(refreshToken, jwtSecretKey);
            User user = userService.getByEmail(email);
            String accessToken = generateAccessToken(user);

            log.info("Generate new access token using a refresh token for user {}", email);
            return TokensResponseDTO.builder()
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UnauthorizedException("Provide a valid refresh token");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateAccessToken(org.springframework.security.core.userdetails.User user) {
        log.info("Create new access token for user {}", user.getUsername());
        return JWT.create()
                .withSubject(user.getUsername()) // User's email is used like username
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .withClaim(TokenValidationService.TOKEN_PERMISSION_CLAIM, user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .sign(Algorithm.HMAC256(jwtSecretKey.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateRefreshToken(org.springframework.security.core.userdetails.User user) {
        log.info("Create new refresh token for user {}", user.getUsername());
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(jwtSecretKey.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateAccessToken(User user) {
        log.info("Create new access token for user {}", user.getEmail());
        return JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .withClaim(TokenValidationService.TOKEN_PERMISSION_CLAIM, user.getRole()
                        .getPermissions()
                        .stream()
                        .map(Permission::getCode)
                        .toList())
                .withClaim(TokenValidationService.TOKEN_USER_CLAIM, user.getId())
                .sign(Algorithm.HMAC256(jwtSecretKey.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoginResponseDTO login(org.springframework.security.core.userdetails.User user) {
        log.info("Login for user {}", user.getUsername());
        User userFromDb = userService.getByEmail(user.getUsername());
        if (!userFromDb.isActive() || !userFromDb.getRole().isActive()) {
            log.error("This user can't login: {}", userFromDb);
            throw new UnauthorizedException("Can't login in this moment :( Please contact assistance department.");
        }

        return LoginResponseDTO.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(generateRefreshToken(user))
                .email(userFromDb.getEmail())
                .roleName(userFromDb.getRole().getName())
                .build();
    }
}
