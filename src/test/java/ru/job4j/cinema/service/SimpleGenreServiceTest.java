package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.genre.GenreRepository;
import ru.job4j.cinema.service.genre.GenreService;
import ru.job4j.cinema.service.genre.SimpleGenreService;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleGenreServiceTest {

    private GenreRepository genreRepository;
    private GenreService genreService;

    @BeforeEach
    public void init() {
        genreRepository = mock(GenreRepository.class);
        genreService = new SimpleGenreService(genreRepository);
    }

    /**
     * Проверяет сценарий возврата данных всех жанров методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetGenres() {
        var genres = IntStream.rangeClosed(1, 3).mapToObj(i -> {
            var genre = new Genre("TestGenre" + i);
            genre.setId(i);
            return genre;
        }).toList();
        when(genreRepository.findAll()).thenReturn(genres);

        var actualGenres = genreService.findAll();

        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(genres);
    }

    /**
     * Проверяет успешный сценарий возврата жанра методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetGenre() {
        var id = 1;
        var genre = new Genre("TestGenre");
        genre.setId(id);
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(genreRepository.findById(integerArgumentCaptor.capture())).thenReturn(Optional.of(genre));

        var actualGenre = genreService.findById(genre.getId());
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualGenre.isPresent()).isTrue();
        assertThat(actualGenre.get()).isEqualTo(genre);
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий возврата жанра методом {@code findById}
     */
    @Test
    void whenFindByInCorrectIdThenGetEmpty() {
        var id = 1;
        when(genreRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        var actualGenre = genreService.findById(id);

        assertThat(actualGenre.isEmpty()).isTrue();
    }
}