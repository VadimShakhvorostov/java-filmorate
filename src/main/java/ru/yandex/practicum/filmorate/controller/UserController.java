package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.core.joran.conditional.IfAction;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validation(user);
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь - {} - создан", user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (!users.containsKey(newUser.getId())){
            throw new NotFoundException("Пользователь с id " + newUser.getId() + "не найден");
        }
        User oldUser = users.get(newUser.getId());
        if (newUser.getName() != null){
            oldUser.setName(newUser.getName());
        }
        if (newUser.getLogin() != null){
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getBirthday() != null){
            oldUser.setBirthday(newUser.getBirthday());
        }
        if (newUser.getEmail() != null){
            oldUser.setEmail(newUser.getEmail());
        }
        log.info("Пользователь - {} - обновлен", oldUser);
        return oldUser;
    }

    private void validation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Имейл не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Имейл должен содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()){
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")){
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getBirthday() != null){
            if (user.getBirthday().isAfter(LocalDate.now())){
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
        }
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
