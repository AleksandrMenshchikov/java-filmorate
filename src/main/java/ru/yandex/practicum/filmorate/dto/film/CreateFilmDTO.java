package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.MPADTO;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class CreateFilmDTO {
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
    @PastOrPresent
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private Integer duration;
    @Valid
    private List<GenreDTO> genres;
    @NotNull
    @Valid
    private MPADTO mpa;
}