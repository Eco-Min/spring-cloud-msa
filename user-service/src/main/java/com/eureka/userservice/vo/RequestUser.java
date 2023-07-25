package com.eureka.userservice.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestUser {

    @NotNull(message = "Email can not be null")
    @Size(min = 2, message = "email not be less than 2 char")
    @Email
    private String email;

    @NotNull(message = "Name can not be null")
    @Size(min = 2, message = "email not be less than 2 char")
    private String name;

    @NotNull(message = "Password can not be null")
    @Size(min =8, message = "Password must be less than 8 char")
    private String pwd;
}
