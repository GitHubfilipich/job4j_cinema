package ru.job4j.cinema.service.film;

import ru.job4j.cinema.dto.FilmDto;

import java.util.Collection;
import java.util.Optional;

public interface FilmService {

    Collection<FilmDto> findAll();

    Optional<FilmDto> findById(int id);
}
