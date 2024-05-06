package com.microservice_level_up.module.auth;

import com.microservice_level_up.error.http_exeption.UnauthorizedException;
import com.microservice_level_up.module.auth.dtos.LoginResponseDTO;
import com.microservice_level_up.module.auth.dtos.TokensResponseDTO;
import com.microservice_level_up.module.user.User;

public interface IAuthService {

    /**
     * Read the refresh token from authorization header and create another valid access token.
     *
     * @param authorizationHeader The authorization header with the bearer refresh token.
     * @return a new access token and the same refresh token.
     * @throws UnauthorizedException          If the provided token is invalid.
     * @see #generateAccessToken(org.springframework.security.core.userdetails.User)
     */
    TokensResponseDTO refreshToken(String authorizationHeader);

    /**
     * Generate a new access token for the given user. This token contains the email and permissions code list from
     * the user.
     *
     * @param user A user authentication
     * @return a new access token
     */
    String generateAccessToken(org.springframework.security.core.userdetails.User user);

    /**
     * Generate a new refresh token for the given user. This token only contains the email. But has a longer lifetime
     * than an access token.
     *
     * @param user A user authentication
     * @return a new refresh token
     */
    String generateRefreshToken(org.springframework.security.core.userdetails.User user);

    /**
     * Generate a new access token for the given user. This token contains the email and permissions code list from
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
     * @see com.microservice_level_up.module.user.IUserService#getByEmail(String)
     */
    LoginResponseDTO login(org.springframework.security.core.userdetails.User user);

    String getJwtSecretKey();
}
