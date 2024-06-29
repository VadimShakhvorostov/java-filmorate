package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FilmTest {

    InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    UserService userService = new UserService(inMemoryUserStorage);
    UserController userController = new UserController(userService);
    FilmService filmService = new FilmService(inMemoryFilmStorage, userService);
    FilmController filmController = new FilmController(filmService);
    Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createFilm() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(90).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }

    @Test
    void createFilmDescriptionLength201() {
        String description = "1".repeat(201);
        Film film = Film.builder().name("test").description(description).releaseDate(LocalDate.of(2000, 12, 22)).duration(90).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(violations);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void createFilmDescriptionLength200() {
        String description = "1".repeat(200);
        Film film = Film.builder().name("test").description(description).releaseDate(LocalDate.of(2000, 12, 22)).duration(90).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }

    @Test
    void createFilmReleaseDateBeforeMinimum() {
        Film film = Film.builder().name("test").description("description").releaseDate(LocalDate.of(1895, 12, 27)).duration(90).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void createFilmReleaseDateAfterMinimum() {
        Film film = Film.builder().name("test").description("description").releaseDate(LocalDate.of(1895, 12, 29)).duration(90).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }

    @Test
    void createFilmDurationNegative() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.of(2000, 12, 22)).duration(-1).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void createFilmDurationPositive() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(1).build();
        Assertions.assertEquals(film, filmController.addFilm(film));
    }

    @Test
    void addLikeFilm() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(1).build();
        User user1 = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        filmController.addFilm(film);
        userController.create(user1);
        filmController.addLikeFilm(film.getId(), user1.getId());
        Assertions.assertTrue(film.getLikes().contains(user1.getId()));
    }

    @Test
    void addLikeUnknownUserFilm() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(1).build();
        filmController.addFilm(film);
        int idUnknownUser = 69;
        Assertions.assertThrows(NotFoundException.class, () -> filmController.addLikeFilm(film.getId(), idUnknownUser));
    }

    @Test
    void removeLikeFilm() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(1).build();
        User user1 = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        filmController.addFilm(film);
        userController.create(user1);
        filmController.addLikeFilm(film.getId(), user1.getId());
        filmController.removeLikeFilm(film.getId(), user1.getId());
        Assertions.assertTrue(film.getLikes().isEmpty());
    }

    @Test
    void removeLikeUnknownFilm() {
        int idUnknownFilm = 69;
        User user1 = User.builder()
                .email("test@email.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Assertions.assertThrows(NotFoundException.class, () -> filmController.removeLikeFilm(idUnknownFilm, user1.getId()));
    }

    @Test
    void removeLikeUnknownUser() {
        Film film = Film.builder().name("test").description("test").releaseDate(LocalDate.now()).duration(1).build();
        filmController.addFilm(film);
        int idUnknownUser = 69;
        Assertions.assertThrows(NotFoundException.class, () -> filmController.removeLikeFilm(film.getId(), idUnknownUser));
    }

    @Test
    void getPopularFilm() {
        Film film1 = Film.builder().name("test1").description("test1").releaseDate(LocalDate.now()).duration(1).build();
        Film film2 = Film.builder().name("test2").description("test2").releaseDate(LocalDate.now()).duration(1).build();
        Film film3 = Film.builder().name("test3").description("test3").releaseDate(LocalDate.now()).duration(1).build();

        film1.getLikes().add(1);
        film1.getLikes().add(2);

        film2.getLikes().add(1);
        film2.getLikes().add(2);
        film2.getLikes().add(3);

        film3.getLikes().add(1);
        film3.getLikes().add(2);
        film3.getLikes().add(3);
        film3.getLikes().add(4);

        filmController.addFilm(film1);
        filmController.addFilm(film2);
        filmController.addFilm(film3);

        int count2 = 2;
        int count3 = 3;

        Assertions.assertEquals(count2, filmController.getPopularFilms(count2).size());
        Assertions.assertEquals(count3, filmController.getPopularFilms(count3).size());
    }

}
