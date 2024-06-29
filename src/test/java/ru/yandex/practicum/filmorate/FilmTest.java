package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmTest {
    FilmController filmController = new FilmController();

    @Test
    void createFilm() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(90).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }

    @Test
    void createFilmDescriptionLength201() {
        String description = "1".repeat(201);
        Film film = Film.builder().name("test").description(description).releaseDate(LocalDate.now()).duration(90).build();
        ValidationException v = Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));

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
        ValidationException v = Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void createFilmReleaseDateAfterMinimum() {
        Film film = Film.builder().name("test").description("description").releaseDate(LocalDate.of(1895, 12, 29)).duration(90).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }

    @Test
    void createFilmDurationNegative() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(-1).build();
        ValidationException v = Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void createFilmDurationPositive() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(1).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }
}
