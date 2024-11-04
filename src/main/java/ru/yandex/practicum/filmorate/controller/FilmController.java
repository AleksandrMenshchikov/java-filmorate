package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setName(film.getName());
        film.setDescription(film.getDescription());

        if (film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            film.setReleaseDate(film.getReleaseDate());
        } else {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        film.setDuration(film.getDuration());
        film.setId(Id.generateId(films));
        films.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Film currentFilm = films.get(film.getId());

        if (currentFilm != null) {
            currentFilm.setName(film.getName());
            currentFilm.setDescription(film.getDescription());

            if (film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))
                    && !currentFilm.getReleaseDate().equals(film.getReleaseDate())) {
                currentFilm.setReleaseDate(film.getReleaseDate());
            } else {
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
            }

            currentFilm.setDuration(film.getDuration());
        } else {
            throw new ValidationException("Фильм по данному id не найден");
        }

        return currentFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }
}
