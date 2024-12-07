package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.enums.MPAType;
import ru.yandex.practicum.filmorate.repository.mappers.MPARowMapper;

import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MPARepository.class,
        MPARowMapper.class
})
class MPARepositoryTest {
    private final MPARepository mpaRepository;

    @Test
    void findOneById() {
        String s = "MPA с id=%s не найден.";
        for (MPAType value : MPAType.values()) {
            long id = (value.ordinal() + 1);
            MPA mpa = mpaRepository.findOneById(id).orElseThrow(() -> new NotFoundException(String.format(s, id)));
            Assertions.assertEquals(mpa.getId(), id);
            Assertions.assertEquals(mpa.getName(), value.getValue());
        }
    }

    @Test
    void findAll() {
        List<MPA> mpaList = mpaRepository.findAll();
        Assertions.assertEquals(mpaList.size(), MPAType.values().length);
    }
}