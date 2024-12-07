package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.repository.MPARepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MPAService {
    private final MPARepository mpaRepository;

    public MPA getMPAById(Long id) {
        return mpaRepository.findOneById(id).orElseThrow(() -> new NotFoundException(String.format("MPA с id=%s не найден.", id)));
    }

    public List<MPA> getAllMPA() {
        return mpaRepository.findAll().stream().sorted(Comparator.comparing(MPA::getId)).toList();
    }
}
