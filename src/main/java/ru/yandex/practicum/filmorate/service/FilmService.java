package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Qualifier("InMemoryFilmStorage")
    private final FilmStorage storage;
    private final UserService userService;
    private int counterId;
    private static final LocalDate MINIMUM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        validation(film);
        film.setId(getNextId());
        storage.addFilm(film);
        return film;
    }

    public Collection<Film> getAllFilms() {
        return storage.getAllFilms();
    }


    public Film updateFilm(Film newFilm) {
        validation(newFilm);
        storage.updateFilm(newFilm);
        return newFilm;
    }

    public void addLikeFilm(Integer id, Integer userId) {
        Film film = storage.getFilmById(id);
        User user = userService.getUserById(userId);
        film.getLikes().add(user.getId());
    }

    public void removeLikeFilm(Integer id, Integer userId) {
        Film film = storage.getFilmById(id);

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не ставил лайк");
        }

        storage.getFilmById(id).getLikes().remove(userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return storage.getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getLikeCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(int id) {
        return storage.getFilmById(id);
    }

    private void validation(Film film) {
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может содержать более 200 символов");
        }
        if (film.getReleaseDate().isBefore(MINIMUM_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность не может быть отрицательной");
        }
    }

    private int getNextId() {
        return ++counterId;
    }

}
