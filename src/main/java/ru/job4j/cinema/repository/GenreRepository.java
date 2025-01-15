package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Genre;

import java.util.Optional;

public interface GenreRepository {

    Optional<Genre> findById(int id);
}
