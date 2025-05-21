package com.example.trainingsystem.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationInfo {
    private boolean hasRoleAdmin;
    private boolean hasRoleUser;
    private boolean isAuthenticated;
}
