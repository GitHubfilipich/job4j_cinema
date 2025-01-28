package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;

import java.util.Collection;
import java.util.Optional;

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

        // TODO - может исправить на поиск из списка всех жанров по ID - ???

        return filmRepository.findAll()
                .stream()
                .map(this::filmDtoFromFilm).toList();
    }

    private FilmDto filmDtoFromFilm(Film film) {
        var genre = genreService.findById(film.getGenreId())
                .map(Genre::getName)
                .orElse("");
        return new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getYear(), film.getMinimalAge(),
                film.getDurationInMinutes(), film.getFileId(), genre);
    }

    @Override
    public Optional<FilmDto> findById(int id) {
        return filmRepository.findById(id)
                .map(this::filmDtoFromFilm);
    }
}
