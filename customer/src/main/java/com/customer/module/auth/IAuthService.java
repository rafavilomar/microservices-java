package com.customer.module.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public interface IAuthService {

    /**
     * Verify and decode the given token to get the username from it.
     *
     * @param token The access or refresh token
     * @return the username
     */
    String getUsername(String token);

    /**
     * Verify and decode the given token to get all user's permissions code from it.
     *
     * @param token The access token
     * @return a list of user's permissions
     */
    List<SimpleGrantedAuthority> getPermissions(String token);
}
