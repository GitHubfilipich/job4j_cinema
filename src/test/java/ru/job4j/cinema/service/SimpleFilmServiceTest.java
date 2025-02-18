package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.film.FilmRepository;
import ru.job4j.cinema.service.film.FilmService;
import ru.job4j.cinema.service.film.SimpleFilmService;
import ru.job4j.cinema.service.genre.GenreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFilmServiceTest {

    private FilmRepository filmRepository;
    private GenreService genreService;
    private FilmService filmService;

    @BeforeEach
    public void init() {
        filmRepository = mock(FilmRepository.class);
        genreService = mock(GenreService.class);
        filmService = new SimpleFilmService(filmRepository, genreService);
    }

    /**
     * Проверяет сценарий возврата данных всех фильмов методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetFilmDTOs() {
        var genres = List.of(new Genre("test1"), new Genre("test2"), new Genre("test3"));
        for (int i = 0; i < 3; i++) {
            genres.get(i).setId(i + 1);
        }
        when(genreService.findAll()).thenReturn(genres);
        var films = List.of(new Film("test1", "testDescription1", 2000, 1, 16, 100, 1),
                new Film("test2", "testDescription2", 2001, 2, 18, 120, 2),
                new Film("test3", "testDescription3", 2002, 3, 21, 180, 3));
        for (int i = 0; i < 3; i++) {
            films.get(i).setId(i + 1);
        }
        when(filmRepository.findAll()).thenReturn(films);
        List<FilmDto> filmDtos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            var film = films.get(i);
            var genre = genres.get(i);
            filmDtos.add(new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getYear(),
                    film.getMinimalAge(), film.getDurationInMinutes(), film.getFileId(), genre.getName()));
        }

        var actualFilmDto = filmService.findAll();

        assertThat(actualFilmDto).containsExactlyInAnyOrderElementsOf(filmDtos);
    }

    /**
     * Проверяет успешный сценарий возврата данных фильма по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetFilmDTO() {
        var id = 1;
        var genre = new Genre("test1");
        genre.setId(id);
        var integerArgumentCaptorGenre = ArgumentCaptor.forClass(Integer.class);
        when(genreService.findById(integerArgumentCaptorGenre.capture())).thenReturn(Optional.of(genre));
        var film = new Film("test1", "testDescription1", 2000, id, 16, 100, 1);
        film.setId(id);
        var integerArgumentCaptorFilm = ArgumentCaptor.forClass(Integer.class);
        when(filmRepository.findById(integerArgumentCaptorFilm.capture())).thenReturn(Optional.of(film));
        var filmDto = new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getYear(),
                film.getMinimalAge(), film.getDurationInMinutes(), film.getFileId(), genre.getName());

        var actualFilmDto = filmService.findById(id);
        var actualIdGenre = integerArgumentCaptorGenre.getValue();
        var actualIdFilm = integerArgumentCaptorFilm.getValue();

        assertThat(actualIdGenre).isEqualTo(id);
        assertThat(actualIdFilm).isEqualTo(id);
        assertThat(actualFilmDto.isPresent()).isTrue();
        assertThat(actualFilmDto.get()).isEqualTo(filmDto);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных фильма по id методом {@code findById}
     */
    @Test
    void whenFindByInCorrectIdThenGetEmpty() {
        var id = 1;
        when(genreService.findById(any(Integer.class))).thenReturn(Optional.empty());
        when(filmRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        var actualFilmDto = filmService.findById(id);

        assertThat(actualFilmDto.isEmpty()).isTrue();
    }
}