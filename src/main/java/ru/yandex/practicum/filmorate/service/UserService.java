package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Qualifier("InMemoryUserStorage")
    private final UserStorage storage;
    private int counterId;

    @Autowired()
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public void addAsFriend(int id, int friendId) {
        storage.getUserById(id).getFriendsId().add(friendId);
        storage.getUserById(friendId).getFriendsId().add(id);
    }

    public void removeFromFriends(int id, int friendId) {
        storage.getUserById(id).getFriendsId().remove(friendId);
        storage.getUserById(friendId).getFriendsId().remove(id);
    }

    public Collection<User> findFriends(int id) {
        Collection<User> userList = storage.findAll();
        Collection<Integer> friends = storage.getUserById(id).getFriendsId();
        return userList.stream().filter(user -> friends.contains(user.getId())).collect(Collectors.toList());
    }

    public Collection<User> mutualFriends(Integer id, Integer otherId) {
        Collection<Integer> friendsId = storage.getUserById(id).getFriendsId();
        Collection<Integer> otherFriendsId = storage.getUserById(otherId).getFriendsId();
        Collection<Integer> mutualFriendsId = friendsId.stream()
                .filter(otherFriendsId::contains)
                .toList();
        return storage.findAll().stream()
                .filter(user -> mutualFriendsId.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public User create(User user) {
        validation(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        return storage.create(user);
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User update(User newUser) {
        validation(newUser);
        return storage.update(newUser);
    }

    public User getUserById(int id) {
        return storage.getUserById(id);
    }

    private void validation(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday() != null && (user.getBirthday().isAfter(LocalDate.now()))) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private int getNextId() {
        return ++counterId;
    }
}
