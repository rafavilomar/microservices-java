package com.security.module.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.security.error.exception.UnauthorizedException;
import com.security.module.auth.dtos.LoginResponseDTO;
import com.security.module.auth.dtos.TokensResponseDTO;
import com.security.module.role.IRoleService;
import com.security.module.role.entites.Permission;
import com.security.module.user.IUserService;
import com.security.module.user.User;
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
    private final IRoleService roleService;

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;
    private static final int ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000; // 1 hour
    private static final int REFRESH_TOKEN_EXPIRATION = 12 * 60 * 60 * 1000; //12 hours
    private static final String TOKEN_ISSUER = "rafavilomar";
    private static final String TOKEN_PERMISSION_CLAIM = "permissions";

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("User {} is trying to access the system", email);
        User user = userService.findEntityByEmail(email);

        if (!user.isActive()) {
            log.error("Can't login because this user is inactive {}", email);
            throw new UnauthorizedException("Inactive user: " + email);
        }
        if (!user.getRole().isActive()) {
            log.error("Can't login, this user has an inactive role {}", user.getRole().getName());
            throw new UnauthorizedException("This user " + email + " has an inactive role");
        }

        return mapUserDetails(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokensResponseDTO refreshToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("There is no refresh bearer token {}", authorizationHeader);
            throw new UnauthorizedException("There is no refresh bearer token in Authorization header");
        }

        try {
            String refreshToken = authorizationHeader.substring("Bearer ".length());

            String username = getUsername(refreshToken);
            User user = userService.findEntityByEmail(username);
            String accessToken = generateAccessToken(user);

            log.info("Generate new access token using a refresh token for user {}", username);
            return TokensResponseDTO.builder()
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UnauthorizedException("Invalid refresh token in Authorization header");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateAccessToken(org.springframework.security.core.userdetails.User user) {
        log.info("Create new access token for user {}", user.getUsername());
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .withIssuer(TOKEN_ISSUER)
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
                .withIssuer(TOKEN_ISSUER)
                .sign(Algorithm.HMAC256(jwtSecretKey.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername(String token) {
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
                .withIssuer(TOKEN_ISSUER)
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
        User userFromDb = userService.findEntityByEmail(user.getUsername());
        return LoginResponseDTO.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(generateRefreshToken(user))
                .email(userFromDb.getEmail())
                .fullName(userFromDb.getFullName())
                .roleName(userFromDb.getRole().getName())
                .permissions(roleService.getPermissionCodeListByRole(userFromDb.getRole().getId()))
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
