package com.hitesh.employeemanagement.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshResponse {

    private String accessToken;
}