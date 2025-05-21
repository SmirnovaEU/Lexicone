package com.example.trainingsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности Spring Security.
 * <p>
 * Включает фильтрацию доступа по URL, настройку авторизации через логин-форму,
 * поддержку "запомнить меня" (remember-me) и защиту паролей через BCrypt.
 * </p>
 *
 * <p>Аннотация {@code @EnableWebSecurity} включает интеграцию Spring Security с MVC.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailSecurityService userService;

    /**
     * Сервис, предоставляющий данные о пользователях для Spring Security.
     *
     * @param userService бин пользовательского сервиса безопасности
     */
    @Autowired
    public SecurityConfig(UserDetailSecurityService userService) {
        this.userService = userService;
    }

    /**
     * Ключ для механизма "запомнить меня".
     * Загружается из конфигурации (application.yml → {@code security.remember-me-key}).
     */
    @Value("${security.remember-me-key}")
    private String rememberMeKey;

    /**
     * Основной фильтр цепочки безопасности приложения.
     * <p>
     * Определяет:
     * <ul>
     *     <li>Разрешённые и защищённые маршруты</li>
     *     <li>Авторизацию через {@code formLogin}</li>
     *     <li>Поддержку "запомнить меня" через cookie</li>
     * </ul>
     * </p>
     *
     * @param http объект {@link HttpSecurity}, предоставляемый Spring
     * @return настроенная цепочка фильтров
     * @throws Exception при ошибке конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/logout").anonymous()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/users/*").hasRole("ADMIN")
                        .requestMatchers("/words/*", "/trainings/**", "/dicts/*").hasRole("USER")
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/")
                )
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                );
        return http.build();
    }

    /**
     * Менеджер аутентификации, который использует {@link UserDetailSecurityService}
     * и {@link BCryptPasswordEncoder} для проверки паролей.
     *
     * @param http контекст безопасности
     * @return бин менеджера аутентификации
     * @throws Exception при ошибке конфигурации
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    /**
     * Кодировщик паролей на основе BCrypt.
     *
     * @return бин {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
