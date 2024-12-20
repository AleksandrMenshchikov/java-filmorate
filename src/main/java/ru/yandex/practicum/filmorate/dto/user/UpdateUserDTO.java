package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
