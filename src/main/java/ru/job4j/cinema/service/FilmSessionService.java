package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmSessionDTO;

import java.util.Collection;
import java.util.Optional;

public interface FilmSessionService {

    Optional<FilmSessionDTO> findById(int id);

    Collection<FilmSessionDTO> findAll();
}
