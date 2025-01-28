package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SimpleTicketService implements TicketService {

    private final TicketRepository ticketRepository;

    public SimpleTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Collection<Ticket> findBySessionId(int sessionId) {
        return ticketRepository.findBySessionId(sessionId);
    }

    @Override
    public Map<Integer, Long> numberOfTicketsForSessions() {
        return ticketRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Ticket::getId, Collectors.counting()));
    }
}
