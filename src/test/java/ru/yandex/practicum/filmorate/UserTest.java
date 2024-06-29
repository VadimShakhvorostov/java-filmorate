package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserTest {

    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    UserService userService = new UserService(inMemoryUserStorage);
    UserController userController = new UserController(userService);
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

    @Test
    void addFriend() {
        User user1 = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .email("test2@email.ru")
                .login("test2")
                .name("test2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user1);
        userController.create(user2);
        userController.аddAsFriend(user1.getId(), user2.getId());
        Assertions.assertEquals(user1.getFriendsId().contains(user2.getId()),
                user2.getFriendsId().contains(user1.getId()));
    }

    @Test
    void addFriendUnknownFriend() {
        User user1 = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        int unknownId = 69;
        userService.create(user1);
        Assertions.assertThrows(NotFoundException.class,
                () -> userController.аddAsFriend(user1.getId(), unknownId));
    }

    @Test
    void deleteFriends() {
        User user1 = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .email("test2@email.ru")
                .login("test2")
                .name("test2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user1);
        userController.create(user2);
        userController.аddAsFriend(user1.getId(), user2.getId());
        userController.removeFromFriends(user1.getId(), user2.getId());
        Assertions.assertEquals(user1.getFriendsId().contains(user2.getId()),
                user2.getFriendsId().contains(user1.getId()));
    }

    @Test
    void getFriends() {
        User user1 = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .email("test2@email.ru")
                .login("test2")
                .name("test2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user1);
        userController.create(user2);
        userController.аddAsFriend(user1.getId(), user2.getId());
        Collection<User> friends = Arrays.asList(user2);
        Assertions.assertEquals(userController.findFriends(user1.getId()), friends);
    }

    @Test
    void getMutualFriends() {
        User user1 = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user2 = User.builder()
                .email("test2@email.ru")
                .login("test2")
                .name("test2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User user3 = User.builder()
                .email("test3@email.ru")
                .login("test3")
                .name("test3")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user1);
        userController.create(user2);
        userController.create(user3);
        userController.аddAsFriend(user1.getId(), user2.getId());
        userController.аddAsFriend(user3.getId(), user2.getId());
        Collection<User> friends = Arrays.asList(user2);
        Assertions.assertEquals(userController.mutualFriends(user1.getId(), user3.getId()), friends);

    }

}
