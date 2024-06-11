package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmTest {
    FilmController filmController = new FilmController();
    boolean exception = false;

    @Test
    void createFilm() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(90).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }

    @Test
    void createFilmWithoutName() {
        try {
            Film film = Film.builder().description("test").releaseDate(LocalDate.now()).duration(90).build();
        } catch (NullPointerException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
    }

    @Test
    void createFilmDescriptionLength201() {
        String description = "1".repeat(201);
        Film film = Film.builder().name("test").description(description).releaseDate(LocalDate.now()).duration(90).build();
        try {
            filmController.addFilm(film);
        } catch (ValidationException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
    }

    @Test
    void createFilmDescriptionLength200() {
        String description = "1".repeat(200);
        Film film = Film.builder().name("test").description(description).releaseDate(LocalDate.now()).duration(90).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }

    @Test
    void createFilmReleaseDateBeforeMinimum() {
        Film film = Film.builder().name("test").description("description").releaseDate(LocalDate.of(1895, 12, 27)).duration(90).build();
        try {
            filmController.addFilm(film);
        } catch (ValidationException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
    }

    @Test
    void createFilmReleaseDateAfterMinimum() {
        Film film = Film.builder().name("test").description("description").releaseDate(LocalDate.of(1895, 12, 29)).duration(90).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }

    @Test
    void createFilmDurationNegative() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(-1).build();
        try {
            filmController.addFilm(film);
        } catch (ValidationException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
    }

    @Test
    void createFilmDurationPositive() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(1).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }
}
