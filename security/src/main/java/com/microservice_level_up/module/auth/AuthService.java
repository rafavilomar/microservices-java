package com.microservice_level_up.module.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService, IAuthService {

    private final IUserService userService;

    @Value("${ClaveSecreta-JWT}")
    private String jwtSecretKey;

    /** 10 minutes */
    private static final int ACCESS_TOKEN_EXPIRATION = 10 * 60 * 1000;
    /** 12 hours */
    private static final int REFRESH_TOKEN_EXPIRATION = 12 * 60 * 60 * 1000;
    private static final String TOKEN_PERMISSION_CLAIM = "permissions";

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("User {} is trying to access the system", email);
        User user = userService.getByEmail(email);

        if (!user.isActive() || !user.getRole().isActive()) {
            log.error("This user can't login: {}", user);
            throw new UnauthorizedException("Can't login in this moment :( Please contact assistance department.");
        }

        return mapUserDetails(user);
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

            String email = getEmail(refreshToken);
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
                .withClaim(TOKEN_PERMISSION_CLAIM, user.getAuthorities()
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
    public String getEmail(String token) {
        return decodedJWT(token).getSubject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SimpleGrantedAuthority> getPermissions(String token) {
        String[] permissions = decodedJWT(token)
                .getClaim(TOKEN_PERMISSION_CLAIM)
                .asArray(String.class);

        return Arrays.stream(permissions)
                .map(SimpleGrantedAuthority::new)
                .toList();
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
                .withClaim(TOKEN_PERMISSION_CLAIM, user.getRole()
                        .getPermissions()
                        .stream()
                        .map(Permission::getCode)
                        .toList())
                .sign(Algorithm.HMAC256(jwtSecretKey.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoginResponseDTO login(org.springframework.security.core.userdetails.User user) {
        log.info("Login for user {}", user.getUsername());
        User userFromDb = userService.getByEmail(user.getUsername());
        return LoginResponseDTO.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(generateRefreshToken(user))
                .email(userFromDb.getEmail())
                .roleName(userFromDb.getRole().getName())
                .build();
    }

    private DecodedJWT decodedJWT(String token) {
        log.info("Verify and decode token");
        JWTVerifier verifier = JWT
                .require(Algorithm.HMAC256(jwtSecretKey.getBytes()))
                .build();
        return verifier.verify(token);
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
}
