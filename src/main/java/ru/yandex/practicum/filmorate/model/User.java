package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;
    @NotBlank(message = "Имаил не может быть пустым")
    @Email(message = "Неверный формат имайл")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent(message = "Дата рождения не может быть в прошлом")
    private LocalDate birthday;
    private final Set<Integer> friendsId = new HashSet<>();
}
