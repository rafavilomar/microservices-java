package com.security.module.user;

import com.security.error.exception.UnauthorizedException;
import com.security.module.user.dto.NewUserDto;
import com.security.module.user.dto.UserResponseDto;

public interface IUserService {
    long create(NewUserDto newUser);
    UserResponseDto findByEmail(String email);
    User findEntityByEmail(String email);

    /**
     * Get the logged user. Could be no logged user in Security Context, so it will throw an exception.
     *
     * @return the logged user.
     * @throws UnauthorizedException If there is no logged user
     * @see #findEntityByEmail(String)
     */
    User getLoggedUser();
    UserResponseDto getLoggedUserResponse();
}
