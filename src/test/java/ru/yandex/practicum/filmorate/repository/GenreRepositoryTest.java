package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.GenreType;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;

import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreRepository.class,
        GenreRowMapper.class
})
class GenreRepositoryTest {
    private final GenreRepository genreRepository;

    @Test
    void findOneById() {
        String s = "Жанр с id=%s не найден.";
        for (GenreType value : GenreType.values()) {
            long id = (value.ordinal() + 1);
            Genre genre = genreRepository.findOneById(id).orElseThrow(() -> new NotFoundException(String.format(s, id)));
            Assertions.assertEquals(genre.getId(), id);
            Assertions.assertEquals(genre.getName(), value.getValue());
        }
    }

    @Test
    void findAll() {
        List<Genre> genres = genreRepository.findAll();
        Assertions.assertEquals(genres.size(), GenreType.values().length);
    }
}