package com.eureka.userservice.service;

import com.eureka.userservice.domain.UserEntity;
import com.eureka.userservice.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();
}
