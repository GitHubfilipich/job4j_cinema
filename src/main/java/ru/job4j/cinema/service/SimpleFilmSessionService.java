package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.repository.FilmSessionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimpleFilmSessionService implements FilmSessionService {

    private final FilmSessionRepository filmSessionRepository;
    private final HallService hallService;
    private final FilmService filmService;
    private final TicketService ticketService;

    public SimpleFilmSessionService(FilmSessionRepository filmSessionRepository, HallService hallService, FilmService filmService, TicketService ticketService) {
        this.filmSessionRepository = filmSessionRepository;
        this.hallService = hallService;
        this.filmService = filmService;
        this.ticketService = ticketService;
    }

    @Override
    public Optional<FilmSessionDTO> findById(int id) {
        return filmSessionRepository.findById(id)
                .flatMap(filmSession -> filmSessionDTOFromFilmSession(List.of(filmSession))
                .stream()
                .findFirst());
    }

    @Override
    public Collection<FilmSessionDTO> findAll() {
        return filmSessionDTOFromFilmSession(filmSessionRepository.findAll());
    }

    private Collection<FilmSessionDTO> filmSessionDTOFromFilmSession(Collection<FilmSession> filmSessions) {
        Map<Integer, String> hallNames = new HashMap<>();
        Map<Integer, Integer> hallRowCounts = new HashMap<>();
        Map<Integer, Integer> hallPlaceCounts = new HashMap<>();
        hallService.findAll().forEach(hall -> {
            hallNames.put(hall.getId(), hall.getName());
            hallRowCounts.put(hall.getId(), hall.getRowCount());
            hallPlaceCounts.put(hall.getId(), hall.getPlaceCount());
        });
        var filmNames = filmService.findAll()
                .stream()
                .collect(Collectors.toMap(FilmDto::id, FilmDto::name));
        var numberOfTicketsForSessions = ticketService.numberOfTicketsForSessions();
        return filmSessions.stream()
                .map(fs -> new FilmSessionDTO(
                        fs.getId(),
                        fs.getStartTime(),
                        fs.getEndTime(),
                        hallNames.getOrDefault(fs.getHallsId(), ""),
                        filmNames.getOrDefault(fs.getFilmId(), ""),
                        fs.getPrice(),
                        hallRowCounts.getOrDefault(fs.getHallsId(), 0)
                                * hallPlaceCounts.getOrDefault(fs.getHallsId(), 0)
                                - numberOfTicketsForSessions.getOrDefault(fs.getId(), 0),
                        hallRowCounts.getOrDefault(fs.getHallsId(), 0),
                        hallPlaceCounts.getOrDefault(fs.getHallsId(), 0)))
                .toList();
    }
}
