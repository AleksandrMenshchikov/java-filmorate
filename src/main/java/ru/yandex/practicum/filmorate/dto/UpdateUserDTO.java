package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UpdateUserDTO {
    @NotNull
    @Positive
    private Long id;
    @Email
    private String email;
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
}
