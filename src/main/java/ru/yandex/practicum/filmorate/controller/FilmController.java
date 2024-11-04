package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.Id;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    private boolean isValidName(String name) {
        return name != null && !name.isBlank();
    }

    private boolean isValidDescription(String description) {
        return description.length() <= 200;
    }

    private boolean isValidReleaseDate(LocalDate releaseDate) {
        return releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }

    private boolean isValidDuration(Integer duration) {
        return duration > 0;
    }

    private void throwException(String message) {
        log.error(message);
        throw new ValidationException(message);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        Integer duration = film.getDuration();

        if (!isValidName(name)) {
            throwException("Название не может быть пустым");
        }

        if (!isValidDescription(description)) {
            throwException("Максимальная длина описания — 200 символов");
        }

        if (!isValidReleaseDate(releaseDate)) {
            throwException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (!isValidDuration(duration)) {
            throwException("Продолжительность фильма должна быть положительным числом");
        }

        film.setId(Id.generateId(films));
        films.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        Long id = film.getId();
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        Integer duration = film.getDuration();
        Film currentFilm = films.get(id);

        if (currentFilm != null) {
            if (isValidName(name)) {
                currentFilm.setName(name);
            }

            if (isValidDescription(description)) {
                currentFilm.setDescription(description);
            }

            if (isValidReleaseDate(releaseDate)) {
                currentFilm.setReleaseDate(releaseDate);
            }

            if (isValidDuration(duration)) {
                currentFilm.setDuration(duration);
            }
        } else {
            throwException("Фильм по данному id не найден");
        }

        return currentFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }
}
