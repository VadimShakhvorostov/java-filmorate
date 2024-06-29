package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Past;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target(ElementType.FIELD)
@Past
public @interface MinReleaseDate {
    String message() default "Дата релиза не может быть раньше 28 декабря 1895";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}

