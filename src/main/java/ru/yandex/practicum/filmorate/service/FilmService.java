package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.InternalServerErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final UserService userService;

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        Optional<Film> optionalFilm = inMemoryFilmStorage.save(film);

        if (optionalFilm.isEmpty()) {
            throw new InternalServerErrorException("Произошла ошибка при создании фильма.");
        }

        return film;
    }

    public Film updateFilm(Film film) {
        Long id = film.getId();

        if (id == null) {
            throw new BadRequestException("Поле id должно быть в теле запроса.");
        }

        Film filmStorage = getFilmById(film.getId());
        film.setLikes(filmStorage.getLikes());

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        Optional<Film> optionalFilm = inMemoryFilmStorage.save(film);

        if (optionalFilm.isEmpty()) {
            throw new InternalServerErrorException("Произошла ошибка при обновлении фильма.");
        }

        return optionalFilm.get();
    }

    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.findAll();
    }

    public Film getFilmById(Long id) {
        Optional<Film> optionalFilm = inMemoryFilmStorage.findOneById(id);

        if (optionalFilm.isEmpty()) {
            throw new NotFoundException(String.format("Фильм по данному id=%s не найден", id));
        }

        return optionalFilm.get();
    }

    public Film updateFilmLikes(Action action, Long id, Long userId) {
        Film film = getFilmById(id);
        User user = userService.getUserById(userId);
        Set<Long> likes = film.getLikes();

        if (action.equals(Action.ADD)) {
            likes.add(user.getId());
        } else if (action.equals(Action.REMOVE)) {
            likes.remove(user.getId());
        }

        return film;
    }

    public Collection<Film> getPopularFilms(Integer count) {
        Collection<Film> allFilms = getAllFilms();
        return allFilms
                .stream()
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .limit(count == null ? 10 : count)
                .toList();
    }
}
