package ru.job4j.cinema.repository.genre;

import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreRepository {

    Collection<Genre> findAll();

    Optional<Genre> findById(int id);
}
