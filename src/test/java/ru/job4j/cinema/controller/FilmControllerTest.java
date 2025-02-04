package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilmControllerTest {

    private FilmController filmController;
    private FilmService filmService;
    private FilmSessionService filmSessionService;

    @BeforeEach
    void setUp() {
        filmService = mock(FilmService.class);
        filmSessionService = mock(FilmSessionService.class);
        filmController = new FilmController(filmService, filmSessionService);
    }

    /**
     * Проверяет сценарий возврата страницы всех фильмов методом {@code getAll}
     */
    @Test
    void whenGetAllThenGetPageWithFilms() {
        var films = List.of(
                new FilmDto(1, "Test1", "Test1", 2021, 16, 100, 1, "Test1"),
                new FilmDto(2, "Test2", "Test2", 2022, 18, 120, 2, "Test2"),
                new FilmDto(3, "Test3", "Test3", 2023, 21, 130, 3, "Test3")
        );
        when(filmService.findAll()).thenReturn(films);
        var model = new ConcurrentModel();

        var actualResponse = filmController.getAll(model);
        var actualFilms = model.getAttribute("films");

        assertThat(actualResponse).isEqualTo("films/list");
        assertThat(actualFilms).isEqualTo(films);
    }

    /**
     * Проверяет успешный сценарий возврата страницы фильма методом {@code getById}
     */
    @Test
    void whenGetByCorrectIdThenGetPageWithFilm() {
        var film = new FilmDto(1, "Test1", "Test1", 2021, 16, 100, 1, "Test1");
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(filmService.findById(integerArgumentCaptor.capture())).thenReturn(Optional.of(film));
        var model = new ConcurrentModel();

        var actualResponse = filmController.getById(model, id);
        var actualFilm = model.getAttribute("film");
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(actualResponse).isEqualTo("films/film");
        assertThat(actualFilm).isEqualTo(film);
    }

    /**
     * Проверяет неуспешный сценарий возврата страницы фильма методом {@code getById}
     */
    @Test
    void whenGetByInCorrectIdThenGetErrorPage() {
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(filmService.findById(integerArgumentCaptor.capture())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var actualResponse = filmController.getById(model, id);
        var actualMessage = model.getAttribute("message");
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(actualResponse).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Фильм с указанным идентификатором не найден");
    }

    /**
     * Проверяет сценарий возврата страницы фильма методом {@code getSessions}
     */
    @Test
    void whenGetSessionsThenGetPageWithSessions() {
        var sessions = List.of(
                new FilmSessionDTO(1, LocalDateTime.of(2025, 2, 1, 10, 0),
                        LocalDateTime.of(2025, 2, 1, 11, 0),
                        "Test1", "Test1", 100, 1, 1, 2),
                new FilmSessionDTO(2, LocalDateTime.of(2025, 2, 2, 10, 0),
                        LocalDateTime.of(2025, 2, 2, 11, 0),
                        "Test2", "Test2", 101, 2, 2, 3),
                new FilmSessionDTO(3, LocalDateTime.of(2025, 2, 3, 10, 0),
                        LocalDateTime.of(2025, 2, 3, 11, 0),
                        "Test3", "Test3", 102, 3, 3, 4)
        );
        when(filmSessionService.findAll()).thenReturn(sessions);
        var model = new ConcurrentModel();

        var actualResponse = filmController.getSessions(model);
        var actualSessions = model.getAttribute("sessions");

        assertThat(actualResponse).isEqualTo("films/sessions");
        assertThat(actualSessions).isEqualTo(sessions);
    }

    /**
     * Проверяет сценарий возврата страницы покупки билета методом {@code buyTicket}
     */
    @Test
    void buyTicket() {
        var id = 1;
        var actualResponse = filmController.buyTicket(id);

        assertThat(actualResponse).isEqualTo("redirect:/tickets/purchase/" + id);
    }
}