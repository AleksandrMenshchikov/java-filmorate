package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.CreateFilmDTO;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.MPADTO;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDTO;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmsGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.FilmsGenreRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.MPARepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;
    private final MPARepository mpaRepository;
    private final GenreRepository genreRepository;
    private final FilmsGenreRepository filmsGenreRepository;

    public Film createFilm(CreateFilmDTO createFilmDTO) {
        if (createFilmDTO.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        checkMPADTO(createFilmDTO.getMpa());
        Long filmId = filmRepository.insertFilm(createFilmDTO);
        List<GenreDTO> genreDTOList = createFilmDTO.getGenres();

        if (genreDTOList != null) {
            checkGenreDTO(genreDTOList);
            Set<GenreDTO> genreDTOSet = new HashSet<>(genreDTOList);
            filmsGenreRepository.batchInsert(new ArrayList<>(genreDTOSet), filmId);
        }

        return getFilmById(filmId);
    }

    public Film getFilmById(Long filmId) {
        return filmRepository.findOneById(filmId);
    }

    public Film updateFilm(UpdateFilmDTO updateFilmDTO) {
        if (updateFilmDTO.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        checkMPADTO(updateFilmDTO.getMpa());
        Film film = filmRepository.updateFilm(updateFilmDTO);
        List<GenreDTO> genreDTOList = updateFilmDTO.getGenres();

        if (genreDTOList != null) {
            checkGenreDTO(genreDTOList);
            Set<GenreDTO> genreDTOSet = new HashSet<>(genreDTOList);
            List<FilmsGenre> filmsGenreList = filmsGenreRepository.findAllByFilmId(film.getId());
            filmsGenreRepository.batchDelete(filmsGenreList);
            filmsGenreRepository.batchInsert(new ArrayList<>(genreDTOSet), film.getId());
        }

        return film;
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll(Integer.MAX_VALUE, false);
    }

    public Film addLike(Long filmId, Long userId) {
        Optional<Like> oneByFilmIdAndUserId = likeRepository.findOneByFilmIdAndUserId(filmId, userId);

        if (oneByFilmIdAndUserId.isPresent()) {
            return filmRepository.findOneById(filmId);
        } else {
            likeRepository.addLike(filmId, userId);
        }

        return filmRepository.findOneById(filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmRepository.findAll(count, true);
    }

    public Film deleteLike(Long filmId, Long userId) {
        Optional<Like> like = likeRepository.findOneByFilmIdAndUserId(filmId, userId);

        if (like.isEmpty()) {
            return filmRepository.findOneById(filmId);
        } else {
            likeRepository.delete(like.get().getId());
            return filmRepository.findOneById(filmId);
        }
    }

    private void checkMPADTO(MPADTO mpa) {
        Long mpaId = mpa.getId();

        if (mpaId != null) {
            Optional<MPA> oneById = mpaRepository.findOneById(mpaId);

            if (oneById.isEmpty()) {
                throw new BadRequestException(String.format("MPA с id=%s не существует.", mpaId));
            }
        }
    }

    private void checkGenreDTO(List<GenreDTO> genreDTOList) {
        if (genreDTOList != null) {
            List<Long> genresDTOIds = genreDTOList.stream().map(GenreDTO::getId).toList();
            List<Long> genresIds = genreRepository.findAll().stream().map(Genre::getId).toList();

            genresDTOIds.forEach(id -> {
                if (!genresIds.contains(id)) {
                    throw new BadRequestException(String.format("Жанр с id=%s не существует.", id));
                }
            });
        }
    }
}
