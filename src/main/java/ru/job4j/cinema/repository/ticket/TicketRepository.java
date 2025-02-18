package ru.job4j.cinema.repository.ticket;

import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

public interface TicketRepository {

    Optional<Ticket> save(Ticket ticket);

    Collection<Ticket> findBySessionId(int sessionId);

    Collection<Ticket> findAll();
}
