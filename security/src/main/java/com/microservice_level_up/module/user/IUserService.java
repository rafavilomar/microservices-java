package com.microservice_level_up.module.user;

import com.microservice_level_up.module.user.dto.NewUserDto;
import com.microservice_level_up.module.user.dto.UserResponseDto;

public interface IUserService {
    void create(NewUserDto newUser);
    UserResponseDto findByEmail(String email);
}
