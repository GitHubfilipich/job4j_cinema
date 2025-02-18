package ru.job4j.cinema.service.film;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.film.FilmRepository;
import ru.job4j.cinema.service.genre.GenreService;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SimpleFilmService implements FilmService {

    private final FilmRepository filmRepository;

    private final GenreService genreService;

    public SimpleFilmService(FilmRepository filmRepository, GenreService genreService) {
        this.filmRepository = filmRepository;
        this.genreService = genreService;
    }

    @Override
    public Collection<FilmDto> findAll() {
        var genreNames = genreService.findAll().stream()
                .collect(Collectors.toMap(Genre::getId, Genre::getName));
        return filmRepository.findAll().stream()
                .map(film -> new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getYear(),
                        film.getMinimalAge(), film.getDurationInMinutes(), film.getFileId(),
                        genreNames.getOrDefault(film.getGenreId(), "")))
                .toList();
    }

    @Override
    public Optional<FilmDto> findById(int id) {
        return filmRepository.findById(id)
                .map(film -> new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getYear(),
                        film.getMinimalAge(), film.getDurationInMinutes(), film.getFileId(),
                        genreService.findById(film.getGenreId())
                                .map(Genre::getName)
                                .orElse("")));
    }
}
