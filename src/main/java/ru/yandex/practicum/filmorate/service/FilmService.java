package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.CreateFilmDTO;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.MPADTO;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDTO;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.InternalServerErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        Film film = filmRepository.findOneById(filmId);
        List<Long> list = likeRepository.findAllByFilmId(film.getId()).stream().map(Like::getUserId).toList();
        film.setLikes(new HashSet<>(list));
        Set<FilmsGenre> filmsGenreSet = new HashSet<>(filmsGenreRepository.findAllByFilmId(film.getId()));
        List<Genre> genres = new ArrayList<>();

        filmsGenreSet.forEach(filmsGenre -> {
            Long genreId = filmsGenre.getGenreId();
            Genre genre = genreRepository.findOneById(genreId).orElseThrow(() ->
                    new NotFoundException(String.format("Жанр с id=%s не найден.", genreId)));
            genres.add(genre);
        });

        film.setGenres(genres.stream().sorted((a, b) -> (int) (a.getId() - b.getId())).toList());
        Long mpaId = film.getMpa().getId();
        MPA mpa = mpaRepository.findOneById(mpaId).orElseThrow(() ->
                new NotFoundException(String.format("MPA с id=%s не найден.", mpaId)));
        film.setMpa(mpa);
        return film;
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
        List<Film> films = filmRepository.findAll();

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
        return getAllFilms()
                .stream()
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .limit(count == null ? 10 : count)
                .toList();
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
