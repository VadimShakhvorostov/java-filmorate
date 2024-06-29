package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate MINIMUM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int counterId;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм - {} -  создан  ", film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        }
        validation(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм - {} -  обновлен  ", newFilm);
        return newFilm;
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