package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.MPADTO;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmDTO;
import ru.yandex.practicum.filmorate.dto.film.ResponseFilmDTO;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDTO;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.FilmWithLikesGenresMPAMapper;
import ru.yandex.practicum.filmorate.model.FilmWithLikesGenresMPA;
import ru.yandex.practicum.filmorate.model.FilmsGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.FilmWithLikesGenresMPARepository;
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
    private final FilmWithLikesGenresMPARepository filmWithLikesGenresMPARepository;

    public ResponseFilmDTO createFilm(CreateFilmDTO createFilmDTO) {
        if (createFilmDTO.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        checkMPADTO(createFilmDTO.getMpa());
        Long filmId = filmRepository.createFilm(FilmMapper.mapToFilm(createFilmDTO));
        List<GenreDTO> genreDTOList = createFilmDTO.getGenres();

        if (genreDTOList != null) {
            checkGenreDTO(genreDTOList);
            Set<GenreDTO> genreDTOSet = new HashSet<>(genreDTOList);
            filmsGenreRepository.batchInsert(new ArrayList<>(genreDTOSet), filmId);
        }

        return getFilmById(filmId);
    }

    public ResponseFilmDTO getFilmById(Long filmId) {
        return FilmWithLikesGenresMPAMapper.mapToResponseFilmDTO(filmWithLikesGenresMPARepository.findOneById(filmId));
    }

    public ResponseFilmDTO updateFilm(UpdateFilmDTO updateFilmDTO) {
        if (updateFilmDTO.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        checkMPADTO(updateFilmDTO.getMpa());
        Long id = updateFilmDTO.getId();
        FilmWithLikesGenresMPA filmWithLikesGenresMPA = filmWithLikesGenresMPARepository.findOneById(id);
        String name = updateFilmDTO.getName();
        if (name == null || name.isBlank()) {
            updateFilmDTO.setName(filmWithLikesGenresMPA.getName());
        }

        String description = updateFilmDTO.getDescription();
        if (description == null || description.isBlank()) {
            updateFilmDTO.setDescription(filmWithLikesGenresMPA.getDescription());
        }

        LocalDate releaseDate = updateFilmDTO.getReleaseDate();
        if (releaseDate == null) {
            updateFilmDTO.setReleaseDate(filmWithLikesGenresMPA.getReleaseDate());
        }

        Integer duration = updateFilmDTO.getDuration();
        if (duration == null) {
            updateFilmDTO.setDuration(filmWithLikesGenresMPA.getDuration());
        }

        filmRepository.updateFilm(FilmMapper.mapToFilm(updateFilmDTO));
        List<GenreDTO> genreDTOList = updateFilmDTO.getGenres();

        if (genreDTOList != null) {
            checkGenreDTO(genreDTOList);
            Set<GenreDTO> genreDTOSet = new HashSet<>(genreDTOList);
            List<FilmsGenre> filmsGenreList = filmsGenreRepository.findAllByFilmId(filmWithLikesGenresMPA.getId());
            filmsGenreRepository.batchDelete(filmsGenreList);
            filmsGenreRepository.batchInsert(new ArrayList<>(genreDTOSet), filmWithLikesGenresMPA.getId());
        }

        return FilmWithLikesGenresMPAMapper.mapToResponseFilmDTO(filmWithLikesGenresMPARepository.findOneById(filmWithLikesGenresMPA.getId()));
    }

    public List<ResponseFilmDTO> getAllFilms() {
        return FilmWithLikesGenresMPAMapper.mapToResponseFilmDTOList(filmWithLikesGenresMPARepository.findAll(Integer.MAX_VALUE, false));
    }

    public ResponseFilmDTO addLike(Long filmId, Long userId) {
        Optional<Like> oneByFilmIdAndUserId = likeRepository.findOneByFilmIdAndUserId(filmId, userId);

        if (oneByFilmIdAndUserId.isPresent()) {
            return FilmWithLikesGenresMPAMapper.mapToResponseFilmDTO(filmWithLikesGenresMPARepository.findOneById(filmId));
        } else {
            likeRepository.addLike(filmId, userId);
        }

        return FilmWithLikesGenresMPAMapper.mapToResponseFilmDTO(filmWithLikesGenresMPARepository.findOneById(filmId));
    }

    public List<ResponseFilmDTO> getPopularFilms(Integer count) {
        return FilmWithLikesGenresMPAMapper.mapToResponseFilmDTOList(filmWithLikesGenresMPARepository.findAll(count, true));
    }

    public ResponseFilmDTO deleteLike(Long filmId, Long userId) {
        Optional<Like> like = likeRepository.findOneByFilmIdAndUserId(filmId, userId);

        if (like.isEmpty()) {
            return FilmWithLikesGenresMPAMapper.mapToResponseFilmDTO(filmWithLikesGenresMPARepository.findOneById(filmId));
        } else {
            likeRepository.delete(like.get().getId());
            return FilmWithLikesGenresMPAMapper.mapToResponseFilmDTO(filmWithLikesGenresMPARepository.findOneById(filmId));
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
