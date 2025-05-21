package com.example.trainingsystem.security;

import com.example.trainingsystem.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Адаптер для преобразования объекта {@link User} в {@link UserDetails},
 * используемый Spring Security при аутентификации и авторизации.
 * <p>
 * Предоставляет информацию о логине, пароле и правах доступа (ролях) пользователя.
 * Используется в {@link UserDetailSecurityService} для передачи в SecurityContext.
 */
@Data
public class UserSecurity implements UserDetails {

    private User user;

    public UserSecurity(User user) {
        this.user = user;
    }

    /**
     * Возвращает коллекцию ролей пользователя в виде объектов {@link GrantedAuthority}.
     * В данной реализации предполагается, что пользователь имеет одну роль.
     *
     * @return список авторизаций (например, {@code ROLE_USER}, {@code ROLE_ADMIN})
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return new BCryptPasswordEncoder().encode(user.getPassword());
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
