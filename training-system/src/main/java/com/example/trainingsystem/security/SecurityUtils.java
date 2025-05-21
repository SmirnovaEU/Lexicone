package com.example.trainingsystem.security;

import com.example.trainingsystem.model.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Утилитный класс для работы с текущим контекстом безопасности Spring Security.
 * <p>
 * Предоставляет статические методы для получения информации о текущем пользователе,
 * его ролях, аутентификации и служебной информации о доступе.
 */
public class SecurityUtils {
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return  ((UserSecurity) authentication.getPrincipal()).getUser();
    }

    public static boolean hasCurrentUserRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }

    public static AuthenticationInfo getAuthenticationInfo() {
        if (isAuthenticated()) {
            return new AuthenticationInfo(hasCurrentUserRole("ROLE_ADMIN"), hasCurrentUserRole("ROLE_USER"), isAuthenticated());
        } else {
            return new AuthenticationInfo(false, false, false);
        }

    }
}
