package ru.yandex.practicum.filmorate.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ResponseUserDTO {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private List<Long> friends; // usersIds
}
