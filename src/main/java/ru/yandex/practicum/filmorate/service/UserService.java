package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage storage;

    @Autowired()
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public void addAsFriend(int id, int friendId) {
        getUserById(id).getFriendsId().add(friendId);
        getUserById(friendId).getFriendsId().add(id);
    }

    public void removeFromFriends(int id, int friendId) {
        getUserById(id).getFriendsId().remove(friendId);
        getUserById(friendId).getFriendsId().remove(id);
    }

    public Collection<User> findFriends(int id) {
        Collection<User> userList = storage.findAll();
        Collection<Integer> friends = getUserById(id).getFriendsId();
        return userList.stream().filter(user -> friends.contains(user.getId())).collect(Collectors.toList());
    }

    public Collection<User> mutualFriends(Integer id, Integer otherId) {
        Collection<Integer> friendsId = getUserById(id).getFriendsId();
        Collection<Integer> otherFriendsId = getUserById(otherId).getFriendsId();
        Collection<Integer> mutualFriendsId = friendsId.stream()
                .filter(otherFriendsId::contains)
                .toList();
        return storage.findAll().stream()
                .filter(user -> mutualFriendsId.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return storage.create(user);
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User update(User newUser) {
        return storage.update(newUser);
    }

    public User getUserById(int id) {
        return storage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

    }
}
