package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmSessionRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFilmSessionServiceTest {

    private FilmSessionRepository filmSessionRepository;
    private HallService hallService;
    private FilmService filmService;
    private TicketService ticketService;
    private SimpleFilmSessionService filmSessionService;

    private Hall hall;
    private List<FilmSession> filmSessions;
    private FilmDto filmDto;
    private Map<Integer, Integer> numberOfTicketsForSessions;
    private List<FilmSessionDTO> filmSessionDTO;

    @BeforeEach
    public void init() {
        filmSessionRepository = mock(FilmSessionRepository.class);
        hallService = mock(HallService.class);
        filmService = mock(FilmService.class);
        ticketService = mock(TicketService.class);
        filmSessionService = new SimpleFilmSessionService(filmSessionRepository, hallService, filmService, ticketService);
    }

    /**
     * Проверяет успешный сценарий возврата данных сеанса методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetFilmSessionDTO() {
        createFilmSessionDto();
        var id = 1;
        var filmSession = filmSessions.get(0);
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(filmSessionRepository.findById(integerArgumentCaptor.capture()))
                .thenReturn(Optional.of(filmSession));
        when(hallService.findAll()).thenReturn(List.of(hall));
        when(filmService.findAll()).thenReturn(List.of(filmDto));
        when(ticketService.numberOfTicketsForSessions()).thenReturn(numberOfTicketsForSessions);
        var expectedFilmSessionDto = new FilmSessionDTO(filmSession.getId(), filmSession.getStartTime(),
                filmSession.getEndTime(), hall.getName(), filmDto.name(), filmSession.getPrice(),
                hall.getRowCount() * hall.getPlaceCount() - filmSession.getId(), hall.getRowCount(), hall.getPlaceCount());

        var actualFilmSessionDTO = filmSessionService.findById(id);
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualFilmSessionDTO.isPresent()).isTrue();
        assertThat(actualFilmSessionDTO.get()).isEqualTo(expectedFilmSessionDto);
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных сеанса методом {@code findById}
     */
    @Test
    void whenFindByInCorrectIdThenGetEmpty() {
        var id = 1;
        when(filmSessionRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        var actualFilmSessionDTO = filmSessionService.findById(id);

        assertThat(actualFilmSessionDTO.isEmpty()).isTrue();
    }

    /**
     * Проверяет сценарий возврата данных всех сеансов методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetFilmSessionDTO() {
        createFilmSessionDto();
        when(filmSessionRepository.findAll()).thenReturn(filmSessions);
        when(hallService.findAll()).thenReturn(List.of(hall));
        when(filmService.findAll()).thenReturn(List.of(filmDto));
        when(ticketService.numberOfTicketsForSessions()).thenReturn(numberOfTicketsForSessions);

        var actualFilmSessionDTO = filmSessionService.findAll();

        assertThat(actualFilmSessionDTO).containsExactlyInAnyOrderElementsOf(filmSessionDTO);
    }

    private void createFilmSessionDto() {
        var id = 1;
        var film = new Film("test1", "testDescription1", 2000, 1, 16, 100, 1);
        film.setId(id);
        filmDto = new FilmDto(id, film.getName(), film.getDescription(), film.getYear(), film.getMinimalAge(),
                film.getDurationInMinutes(), film.getFileId(), "testGenre");
        hall = new Hall("test1", 5, 6, "testDescription");
        hall.setId(id);
        filmSessions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            var filmSession = new FilmSession(film.getId(), hall.getId(),
                    LocalDateTime.of(2025, 2, 13, 10, 0),
                    LocalDateTime.of(2025, 2, 13, 11, 0), 100);
            filmSession.setId(i);
            filmSessions.add(filmSession);
        }
        filmSessionDTO = new ArrayList<>();
        numberOfTicketsForSessions = new HashMap<>();
        for (int i = 0; i < filmSessions.size(); i++) {
            var filmSession = filmSessions.get(i);
            numberOfTicketsForSessions.put(i, i);
            filmSessionDTO.add(new FilmSessionDTO(i, filmSession.getStartTime(), filmSession.getEndTime(),
                    hall.getName(), film.getName(), filmSession.getPrice(),
                    hall.getPlaceCount() * hall.getRowCount() - i, hall.getRowCount(), hall.getPlaceCount()));
        }
    }
}