package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FileDto;

import java.util.Optional;

public interface FileService {

    Optional<FileDto> findById(int id);
}
