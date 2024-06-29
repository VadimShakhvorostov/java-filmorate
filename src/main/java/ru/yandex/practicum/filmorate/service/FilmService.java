package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage storage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        storage.addFilm(film);
        return film;
    }

    public Collection<Film> getAllFilms() {
        return storage.getAllFilms();
    }


    public Film updateFilm(Film newFilm) {
        storage.updateFilm(newFilm);
        return newFilm;
    }

    public void addLikeFilm(Integer id, Integer userId) {
        Film film = getFilmById(id);
        User user = userService.getUserById(userId);
        film.getLikes().add(user.getId());
    }

    public void removeLikeFilm(Integer id, Integer userId) {
        Film film = getFilmById(id);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не ставил лайк");
        }
        getFilmById(id).getLikes().remove(userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return storage.getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getLikeCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(int id) {
        return storage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
        //return storage.getFilmById(id);
    }
}
