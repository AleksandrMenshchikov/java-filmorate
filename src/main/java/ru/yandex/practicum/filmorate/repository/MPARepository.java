package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

@Repository
public class MPARepository extends BaseRepository<MPA> {
    private static final String FIND_ONE_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";

    public MPARepository(JdbcTemplate jdbc, RowMapper<MPA> mapper) {
        super(jdbc, mapper);
    }

    public Optional<MPA> findOneById(Long id) {
        return findOne(FIND_ONE_BY_ID_QUERY, id);
    }

    public List<MPA> findAll() {
        return findAll(FIND_ALL_QUERY);
    }
}
