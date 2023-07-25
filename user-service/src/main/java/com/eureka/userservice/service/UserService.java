package com.eureka.userservice.service;

import com.eureka.userservice.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
}
