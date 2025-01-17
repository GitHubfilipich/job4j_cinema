package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmDto;

import java.util.Optional;

public interface FilmService {

    Optional<FilmDto> findById(int id);
}
