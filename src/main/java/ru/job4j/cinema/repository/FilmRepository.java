package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Film;

import java.util.Optional;

public interface FilmRepository {

    Optional<Film> findById(int id);
}
