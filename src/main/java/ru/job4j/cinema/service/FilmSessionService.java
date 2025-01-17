package ru.job4j.cinema.service;

import ru.job4j.cinema.model.FilmSession;

import java.util.Collection;
import java.util.Optional;

public interface FilmSessionService {

    Optional<FilmSession> findById(int id);

    Collection<FilmSession> findAll();
}
