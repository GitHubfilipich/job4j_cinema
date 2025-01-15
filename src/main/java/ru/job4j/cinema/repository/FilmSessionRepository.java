package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.FilmSession;

import java.util.Optional;

public interface FilmSessionRepository {

    Optional<FilmSession> findById(int id);
}
