package com.example.trainingsystem.security;

import com.example.trainingsystem.model.User;
import com.example.trainingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис интеграции пользовательских данных с Spring Security.
 * <p>
 * Реализует интерфейс {@link UserDetailsService},
 * который используется Spring Security для загрузки данных пользователя при аутентификации.
 * </p>
 *
 * <p>Находит пользователя по имени и адаптирует его в {@link UserDetails} через {@link UserSecurity}.</p>
 */
@Service
public class UserDetailSecurityService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public UserDetailSecurityService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("user %s not found", username));
        }
        return new UserSecurity(user);
    }

}
