package ru.job4j.cinema.repository.film.session;

import ru.job4j.cinema.model.FilmSession;

import java.util.Collection;
import java.util.Optional;

public interface FilmSessionRepository {

    Optional<FilmSession> findById(int id);

    Collection<FilmSession> findAll();
}
