package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


public class UserTest {
    UserController userController = new UserController();
    boolean exception = false;

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
        try {
            userController.create(user);
        } catch (ValidationException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
    }

    @Test
    void createUserBadLogin() {
        User user = User.builder()
                .email("test@email.ru")
                .login("te   st")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        try {
            userController.create(user);
        } catch (ValidationException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
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
        try {
            userController.create(user);
        } catch (ValidationException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
    }
}
