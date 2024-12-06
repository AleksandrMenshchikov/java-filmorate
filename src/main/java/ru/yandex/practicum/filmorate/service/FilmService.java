package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.CreateFilmDTO;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDTO;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.MPARepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;
    private final MPARepository mpaRepository;
    private final GenreRepository genreRepository;

    public Film createFilm(CreateFilmDTO createFilmDTO) {
        if (createFilmDTO.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        Long mpaId = createFilmDTO.getMpa().getId();
        Optional<MPA> mpa = mpaRepository.findOneById(mpaId);

        if (mpa.isEmpty()) {
            throw new BadRequestException(String.format("MPA с id=%s не существует.", mpaId));
        }

        List<GenreDTO> genres = createFilmDTO.getGenres();

        if (genres != null) {
            for (GenreDTO genreDTO : genres) {
                Long genreDTOId = genreDTO.getId();
                genreRepository.findOneById(genreDTOId).orElseThrow(() -> new BadRequestException(String.format("Жанр с id=%s не существует.", genreDTOId)));
            }
        }

        return filmRepository.createFilm(createFilmDTO);
    }

    public Film updateFilm(UpdateFilmDTO updateFilmDTO) {
        if (updateFilmDTO.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        Long mpaId = updateFilmDTO.getMpa().getId();

        if (mpaId != null) {
            Optional<MPA> mpa = mpaRepository.findOneById(mpaId);

            if (mpa.isEmpty()) {
                throw new BadRequestException(String.format("MPA с id=%s не существует.", mpaId));
            }
        }

        List<GenreDTO> genres = updateFilmDTO.getGenres();

        if (genres != null) {
            for (GenreDTO genreDTO : genres) {
                Long genreDTOId = genreDTO.getId();
                genreRepository.findOneById(genreDTOId).orElseThrow(() -> new BadRequestException(String.format("Жанр с id=%s не существует.", genreDTOId)));
            }
        }

        return filmRepository.updateFilm(updateFilmDTO);
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film getFilmById(Long id) {
        return filmRepository.findOneById(id);
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
}
