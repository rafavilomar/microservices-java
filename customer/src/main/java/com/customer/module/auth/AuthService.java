package com.customer.module.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.customer.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService, IAuthService {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;
    private static final String TOKEN_PERMISSION_CLAIM = "permissions";

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("loadUserByUsername {}", email);
        throw new UnauthorizedException("Not available");
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

    private DecodedJWT decodedJWT(String token) {
        log.info("Verify and decode token");
        JWTVerifier verifier = JWT
                .require(Algorithm.HMAC256(jwtSecretKey.getBytes()))
                .build();
        return verifier.verify(token);
    }
}
