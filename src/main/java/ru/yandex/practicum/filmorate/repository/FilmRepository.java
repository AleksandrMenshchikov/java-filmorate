package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.CreateFilmDTO;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDTO;
import ru.yandex.practicum.filmorate.exception.InternalServerErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmsGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ONE_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private final MPARepository mpaRepository;
    private final GenreRepository genreRepository;
    private final FilmsGenreRepository filmsGenreRepository;
    private final LikeRepository likeRepository;

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, MPARepository mpaRepository, GenreRepository genreRepository, FilmsGenreRepository filmsGenreRepository, LikeRepository likeRepository) {
        super(jdbc, mapper);
        this.mpaRepository = mpaRepository;
        this.genreRepository = genreRepository;
        this.filmsGenreRepository = filmsGenreRepository;
        this.likeRepository = likeRepository;
    }

    public Film createFilm(CreateFilmDTO createFilmDTO) {
        Long id = insert(INSERT_QUERY,
                createFilmDTO.getName(),
                createFilmDTO.getDescription(),
                createFilmDTO.getReleaseDate(),
                createFilmDTO.getDuration(),
                createFilmDTO.getMpa().getId());
        List<GenreDTO> genreDTOList = createFilmDTO.getGenres();

        if (genreDTOList != null) {
            Set<GenreDTO> genreDTOSet = new HashSet<>(genreDTOList);

            for (GenreDTO genreDTO : genreDTOSet) {
                filmsGenreRepository.addGenre(id, genreDTO.getId());
            }
        }

        return findOneById(id);
    }

    public Film findOneById(Long id) {
        Film film = findOne(FIND_ONE_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден.", id)));
        List<Long> list = likeRepository.findAllByFilmId(id).stream().map(Like::getUserId).toList();
        film.setLikes(new HashSet<>(list));
        List<FilmsGenre> filmsGenreList = filmsGenreRepository.findAllByFilmId(id);
        List<Genre> genres = new ArrayList<>();

        for (FilmsGenre filmsGenre : filmsGenreList) {
            Long genreId = filmsGenre.getGenreId();
            Genre genre = genreRepository.findOneById(genreId).orElseThrow(() -> new NotFoundException(String.format("Жанр с id=%s не найден.", genreId)));
            genres.add(genre);
        }

        film.setGenres(genres);
        Long mpaId = film.getMpa().getId();
        MPA mpa = mpaRepository.findOneById(mpaId).orElseThrow(() -> new NotFoundException(String.format("MPA с id=%s не найден.", mpaId)));
        film.setMpa(mpa);
        return film;
    }

    public Film updateFilm(UpdateFilmDTO updateFilmDTO) {
        Long id = updateFilmDTO.getId();
        String name = updateFilmDTO.getName();
        String description = updateFilmDTO.getDescription();
        LocalDate releaseDate = updateFilmDTO.getReleaseDate();
        Integer duration = updateFilmDTO.getDuration();
        Long mpaId = updateFilmDTO.getMpa().getId();
        List<GenreDTO> genresDTO = updateFilmDTO.getGenres();
        Film film = findOneById(id);

        update(UPDATE_QUERY,
                name == null || name.isBlank() ? film.getName() : name,
                description == null || description.isBlank() ? film.getDescription() : description,
                releaseDate == null ? film.getReleaseDate() : releaseDate,
                duration == null ? film.getDuration() : duration,
                mpaId,
                id
        );

        if (genresDTO != null) {
            List<FilmsGenre> filmsGenreList = filmsGenreRepository.findAllByFilmId(id);

            filmsGenreList.forEach(filmsGenre -> {
                filmsGenreRepository.delete(filmsGenre.getId());
            });

            Set<GenreDTO> genreDTOList = new HashSet<>(genresDTO);

            for (GenreDTO genreDTO : genreDTOList) {
                filmsGenreRepository.addGenre(film.getId(), genreDTO.getId());
            }
        }

        return findOneById(id);
    }

    public List<Film> findAll() {
        List<Film> films = findAll(FIND_ALL_QUERY);

        for (Film film : films) {
            List<Long> list = likeRepository.findAllByFilmId(film.getId()).stream().map(Like::getUserId).toList();
            film.setLikes(new HashSet<>(list));
            List<FilmsGenre> filmsGenreList = filmsGenreRepository.findAllByFilmId(film.getId());
            List<Genre> genres = new ArrayList<>();

            for (FilmsGenre filmsGenre : filmsGenreList) {
                Long genreId = filmsGenre.getGenreId();
                Genre genre = genreRepository.findOneById(genreId).orElseThrow(() -> new InternalServerErrorException("Произошла ошибка при получении всех фильмов."));
                genres.add(genre);
            }

            film.setGenres(genres);
            MPA mpa = mpaRepository.findOneById(film.getMpa().getId()).orElseThrow(() -> new InternalServerErrorException("Произошла ошибка при получении всех фильмов."));
            film.setMpa(mpa);
        }

        return films;
    }
}
