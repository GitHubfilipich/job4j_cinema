package ru.job4j.cinema.service.implementation;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.GenreRepository;
import ru.job4j.cinema.service.GenreService;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimpleGenreService implements GenreService {

    private final GenreRepository genreRepository;

    public SimpleGenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Collection<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Optional<Genre> findById(int id) {
        return genreRepository.findById(id);
    }
}
