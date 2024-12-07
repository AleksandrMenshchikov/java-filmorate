package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UpdateFilmDTO {
    @NotNull
    @Positive
    private Long id;
    private String name;
    @Size(max = 200)
    private String description;
    @PastOrPresent
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    @Size(min = 1)
    @Valid
    private List<GenreDTO> genres;
    @Valid
    private MPADTO mpa;
}
