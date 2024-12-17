package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Genre getGenreById(Long id) {
        return genreRepository.findOneById(id).orElseThrow(() ->
                new NotFoundException(String.format("Жанр с id=%s не найден.", id)));
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
