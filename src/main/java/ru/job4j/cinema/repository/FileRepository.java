package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.File;

import java.util.Optional;

public interface FileRepository {

    Optional<File> findById(int id);
}
