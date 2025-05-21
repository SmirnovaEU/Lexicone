# 🛡️ Security Overview

Документация по безопасности для проекта на Spring Boot с использованием Spring Security, ACL и ролевой модели.

---

## 🔑 Аутентификация

- **Механизм:** форма входа (`formLogin`)
- **Хранилище пользователей:** таблица `users`
- **Класс:** `UserDetailSecurityService implements UserDetailsService`
- **Адаптер:** `UserSecurity implements UserDetails`

### 🔄 Логика:

- При входе по имени пользователя выполняется `loadUserByUsername(String username)`
- Пользователь оборачивается в `UserSecurity`, где возвращается:
  - имя (`getUsername()`)
  - пароль (`getPassword()`)
  - роли → `Collection<? extends GrantedAuthority>`

---

## 🧾 Роли и доступ

- Роль определяется в таблице `users`
- Поддержка:
  - `ROLE_USER`
  - `ROLE_ADMIN`

### 🔒 Правила доступа (`SecurityFilterChain`):

| URL                                 | Доступ                         |
|------------------------------------|--------------------------------|
| `/`                                 | Все                            |
| `/login`, `/logout`                | Только анонимные               |
| `/users/*`                         | Только `ROLE_ADMIN`            |
| `/words/*`, `/trainings/**`, `/dicts/*` | Только `ROLE_USER`        |

---

## 🧠 Проверка ролей

Методы в `SecurityUtils`:

```java
<<<<<<< HEAD
getCurrentUser();      // возвращает User
hasCurrentUserRole("ROLE_ADMIN"); // проверка роли
isAuthenticated();     // проверка, что не аноним
getAuthenticationInfo(); // сводка по текущему пользователю
=======
SecurityUtils.getCurrentUser();      // возвращает User
SecurityUtils.hasCurrentUserRole("ROLE_ADMIN"); // проверка роли
SecurityUtils.isAuthenticated();     // проверка, что не аноним
SecurityUtils.getAuthenticationInfo(); // сводка по текущему пользователю
>>>>>>> origin/master
```

---

## 🗝️ ACL (Access Control List)

- Включён уровень объектной безопасности (ACL)
- Реализован через:
  - `JdbcMutableAclService` (в `AclConfig`)
  - `PermissionEvaluator` (в `defaultMethodSecurityExpressionHandler`)
- Используется в методах с аннотациями `@PreAuthorize, @PreFilter, @PostAuthorize, @PostFilter`

### 🔐 Пример:

```java
@PostFilter("hasPermission(filterObject, 'READ')")
public List<Dictionary> getAll() ;
```

---

## 🔁 Remember-Me

- Включён механизм "запомнить меня"
- Конфигурация:
  ```applicatiop.yml
  security:
    remember-me-key: a_secure_remember_me_key
  ```

---

## 🔐 Пароли

Хешируются с помощью `BCryptPasswordEncoder`

---

## 🧩 Точки расширения

| Компонент                         | Назначение                                 |
|----------------------------------|--------------------------------------------|
| `SecurityConfig`                 | Правила фильтрации и логики входа          |
| `UserDetailSecurityService`      | Загрузка пользователя                      |
| `UserSecurity`                   | Адаптер для Spring Security                |
| `AclConfig`                      | Бины ACL, кэш, стратегии                   |
| `AclMethodSecurityConfiguration` | Включает ACL в аннотации @PreAuthorize     |

---

## 📎 Ссылки

- [Spring Security Docs](https://docs.spring.io/spring-security/)
- [Spring Security ACL Reference](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#domain-acl)
