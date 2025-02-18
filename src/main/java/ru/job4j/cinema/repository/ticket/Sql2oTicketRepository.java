package ru.job4j.cinema.repository.ticket;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oTicketRepository implements TicketRepository {

    private final Sql2o sql2o;

    public Sql2oTicketRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                    "INSERT INTO tickets (session_id, row_number, place_number, user_id) VALUES (:session_id, :row_number, :place_number, :user_id)", true)
                    .addParameter("session_id", ticket.getSessionId())
                    .addParameter("row_number", ticket.getRowNumber())
                    .addParameter("place_number", ticket.getPlaceNumber())
                    .addParameter("user_id", ticket.getUserId());
            int generatedId = query.setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeUpdate().getKey(Integer.class);
            ticket.setId(generatedId);
            return Optional.of(ticket);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Ticket> findBySessionId(int sessionId) {
        try (var connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM tickets WHERE session_id = :session_id")
                    .addParameter("session_id", sessionId)
                    .setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetch(Ticket.class);
        }
    }

    @Override
    public Collection<Ticket> findAll() {
        try (var connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM tickets")
                    .setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetch(Ticket.class);
        }
    }
}
