package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;

import java.util.Optional;

@Service
public class SimpleFilmService implements FilmService {

    private final FilmRepository filmRepository;

    private final GenreService genreService;

    private final FileService fileService;

    public SimpleFilmService(FilmRepository filmRepository, GenreService genreService, FileService fileService) {
        this.filmRepository = filmRepository;
        this.genreService = genreService;
        this.fileService = fileService;
    }

    @Override
    public Optional<FilmDto> findById(int id) {
        var filmOptional = filmRepository.findById(id);
        if (filmOptional.isEmpty()) {
            return Optional.empty();
        }
        var film = filmOptional.get();
        var genre = genreService.findById(film.getGenreId()).orElse(new Genre(0, "")).getName();
        var file = fileService.findById(film.getFileId()).orElse(new FileDto("", new byte[0]));
        return Optional.of(new FilmDto(film.getName(), film.getDescription(), film.getYear(), film.getMinimalAge(),
                film.getDurationInMinutes(), genre, file.name(), file.content()));
    }
}
