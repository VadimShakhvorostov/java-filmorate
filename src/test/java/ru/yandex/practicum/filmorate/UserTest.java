package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserTest {
    UserController userController = new UserController();
    Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenValidEmail() {
        User user = User.builder()
                .email("Test@test.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    void whenNullEmail() {
        User user = User.builder()
                .email(null)
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void whenEmptyEmail() {
        User user = User.builder()
                .email(" ")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    void whenBadEmail() {
        User user = User.builder()
                .email(".A@")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void createUser() {
        User user = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Assertions.assertEquals(user, userController.create(user));
    }

    @Test
    void createUserWithoutLogin() {
        User user = User.builder()
                .email("test@email.ru")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        ValidationException v = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void createUserBadLogin() {
        User user = User.builder()
                .email("test@email.ru")
                .login("te   st")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        ValidationException v = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void createUserEmptyName() {
        User user = User.builder()
                .email("test@email.ru")
                .login("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Assertions.assertEquals(user.getLogin(), userController.create(user).getName());
    }

    @Test
    void createUserBirthdayFuture() {
        User user = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2025, 1, 1))
                .build();
        ValidationException v = Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
    }
}
