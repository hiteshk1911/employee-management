package com.hitesh.employeemanagement.auth;

import com.hitesh.employeemanagement.user.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;

    private Role role;
}