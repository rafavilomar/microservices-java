package com.microservice_level_up.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;

public interface TokenValidationService {

    String TOKEN_PERMISSION_CLAIM = "permissions";
    String TOKEN_USER_CLAIM = "user_id";

    /**
     * Verify and decode the given token to get the email from it.
     *
     * @param token The access or refresh token
     * @param jwtSecretKey The secret key to decode the given token
     * @return the email
     */
    static String getEmail(@NotNull String token, @NotNull String jwtSecretKey) {
        return decodedJWT(token, jwtSecretKey).getSubject();
    }

    /**
     * Verify and decode the given token to get all user's permissions code from it.
     *
     * @param token The access token
     * @param jwtSecretKey The secret key to decode the given token
     * @return a list of user's permissions
     */
    static List<SimpleGrantedAuthority> getPermissions(@NotNull String token, @NotNull String jwtSecretKey) {

        String[] permissions = decodedJWT(token, jwtSecretKey)
                .getClaim(TOKEN_PERMISSION_CLAIM)
                .asArray(String.class);

        return Arrays.stream(permissions)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    static DecodedJWT decodedJWT(@NotNull String token, @NotNull String jwtSecretKey) {
        JWTVerifier verifier = JWT
                .require(Algorithm.HMAC256(jwtSecretKey.getBytes()))
                .build();
        return verifier.verify(token);
    }
}
