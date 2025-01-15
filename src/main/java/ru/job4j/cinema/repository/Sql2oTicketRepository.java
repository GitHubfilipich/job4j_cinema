package ru.job4j.cinema.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Ticket;

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
            int generatedId = query.executeUpdate().getKey(Integer.class);
            ticket.setId(generatedId);
            return Optional.of(ticket);
        }
    }

    @Override
    public Optional<Ticket> findBySessionIdAndRowNumberAndPlaceNumber(int sessionId, int rowNumber, int placeNumber) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                    "SELECT * FROM tickets WHERE session_id = :session_id AND row_number = :row_number AND place_number = :place_number");
            var ticket = query
                    .addParameter("session_id", sessionId)
                    .addParameter("row_number", rowNumber)
                    .addParameter("place_number", placeNumber)
                    .executeAndFetchFirst(Ticket.class);
            return Optional.ofNullable(ticket);
        }
    }
}
