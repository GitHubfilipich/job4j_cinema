package ru.job4j.cinema.service.genre;

import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreService {

    Collection<Genre> findAll();

    Optional<Genre> findById(int id);
}
