package com.security.module.auth;

import com.security.error.exception.UnauthorizedException;
import com.security.module.auth.dtos.LoginResponseDTO;
import com.security.module.auth.dtos.TokensResponseDTO;
import com.security.module.role.IRoleService;
import com.security.module.user.IUserService;
import com.security.module.user.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public interface IAuthService {

    /**
     * Read the authorization header to get the refresh token and create another valid access token.
     *
     * @param authorizationHeader The authorization header with the bearer refresh token.
     * @return a new access token and the same refresh token.
     * @throws UnauthorizedException If the authorizationHeader is invalid or is not a bearer token.
     * @see #generateAccessToken(org.springframework.security.core.userdetails.User)
     */
    TokensResponseDTO refreshToken(String authorizationHeader);

    /**
     * Generate a new access token for the given user. This token contains the username and permissions code list from
     * the user.
     *
     * @param user A user authentication
     * @return a new access token
     */
    String generateAccessToken(org.springframework.security.core.userdetails.User user);

    /**
     * Generate a new refresh token for the given user. This token only contains the username. But has a longer lifetime
     * than an access token.
     *
     * @param user A user authentication
     * @return a new refresh token
     */
    String generateRefreshToken(org.springframework.security.core.userdetails.User user);

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

    /**
     * Generate a new access token for the given user. This token contains the username and permissions code list from
     * the user.
     *
     * @param user A user entity from database.
     * @return a new access token
     */
    String generateAccessToken(User user);

    /**
     * Get basic information for login.
     *
     * @param user The user loaded in Security Context
     * @return the login info.
     * @see #generateAccessToken(org.springframework.security.core.userdetails.User)
     * @see #generateRefreshToken(org.springframework.security.core.userdetails.User)
     * @see IUserService#findEntityByEmail(String)
     * @see IRoleService#getPermissionCodeListByRole(long)
     */
    LoginResponseDTO login(org.springframework.security.core.userdetails.User user);
}
