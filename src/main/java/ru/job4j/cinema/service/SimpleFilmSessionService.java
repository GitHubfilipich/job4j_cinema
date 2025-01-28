package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmSessionRepository;

import java.util.Collection;
import java.util.Optional;
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
    public Optional<FilmSession> findById(int id) {
        return filmSessionRepository.findById(id);
    }

    @Override
    public Collection<FilmSessionDTO> findAll() {
        var filmSessions = filmSessionRepository.findAll();
        var halls = hallService.findAll();
        var hallNames = halls.stream()
                .collect(Collectors.toMap(Hall::getId, Hall::getName));
        var hallCapacities = halls.stream()
                .collect(Collectors.toMap(Hall::getId, hall -> (long) hall.getRowCount() * hall.getPlaceCount()));
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
                        hallCapacities.getOrDefault(fs.getHallsId(), 0L)
                                - numberOfTicketsForSessions.getOrDefault(fs.getId(), 0L)))
                .toList();
    }
}
