package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Sql2oTicketRepositoryTest {

    private static Sql2oTicketRepository sql2oTicketRepository;
    private static Sql2o sql2o;
    private static Genre genre;
    private static File file;
    private static Film film;
    private static Hall hall;
    private static Collection<FilmSession> filmSessions;
    private static Collection<Ticket> tickets;

    @BeforeAll
    static void setUp() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oTicketRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oTicketRepository = new Sql2oTicketRepository(sql2o);
        file = new File("TestName", "TestPath");
        genre = new Genre("testGenre");
        hall = new Hall("TestHall", 10, 15, "TestDescription");

        try (var connection = sql2o.open()) {
            int generatedId = connection.createQuery("INSERT INTO files (name, path) VALUES (:name, :path)", true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath())
                    .executeUpdate().getKey(Integer.class);
            file.setId(generatedId);

            generatedId = connection.createQuery("INSERT INTO genres (name) VALUES (:name)", true)
                    .addParameter("name", genre.getName())
                    .executeUpdate().getKey(Integer.class);
            genre.setId(generatedId);

            generatedId = connection.createQuery("INSERT INTO halls (name, row_count, place_count, description)"
                            + " VALUES (:name, :row_count, :place_count, :description)", true)
                    .addParameter("name", hall.getName())
                    .addParameter("row_count", hall.getRowCount())
                    .addParameter("place_count", hall.getPlaceCount())
                    .addParameter("description", hall.getDescription())
                    .executeUpdate().getKey(Integer.class);
            hall.setId(generatedId);

            film = new Film("TestFilm1", "TestDescription1", 2020, genre.getId(),
                    1, 60, file.getId());

            generatedId = connection.createQuery("INSERT INTO films (name, description, \"year\", genre_id, "
                            + "minimal_age, duration_in_minutes, file_id) VALUES (:name, :description, :year, :genre_id, "
                            + " :minimal_age, :duration_in_minutes, :file_id)", true)
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("year", film.getYear())
                    .addParameter("genre_id", film.getGenreId())
                    .addParameter("minimal_age", film.getMinimalAge())
                    .addParameter("duration_in_minutes", film.getDurationInMinutes())
                    .addParameter("file_id", film.getFileId())
                    .executeUpdate().getKey(Integer.class);
            film.setId(generatedId);

            filmSessions = List.of(new FilmSession(film.getId(), hall.getId(),
                            LocalDateTime.of(2025, 2, 9, 10, 0, 0),
                            LocalDateTime.of(2025, 2, 9, 11, 0, 0), 100),
                    new FilmSession(film.getId(), hall.getId(),
                            LocalDateTime.of(2025, 2, 10, 10, 0, 0),
                            LocalDateTime.of(2025, 2, 10, 11, 0, 0), 110),
                    new FilmSession(film.getId(), hall.getId(),
                            LocalDateTime.of(2025, 2, 11, 10, 0, 0),
                            LocalDateTime.of(2025, 2, 11, 11, 0, 0), 120)
            );

            var query = connection.createQuery("INSERT INTO film_sessions (film_id, halls_id, start_time, "
                    + "end_time, price) VALUES (:film_id, :halls_id, :start_time, :end_time, :price)", true);
            for (FilmSession filmSession : filmSessions) {
                generatedId = query.addParameter("film_id", filmSession.getFilmId())
                        .addParameter("halls_id", filmSession.getHallsId())
                        .addParameter("start_time", filmSession.getStartTime())
                        .addParameter("end_time", filmSession.getEndTime())
                        .addParameter("price", filmSession.getPrice())
                        .executeUpdate().getKey(Integer.class);
                filmSession.setId(generatedId);
            }

            int sessionIdFirst = filmSessions.stream().findFirst().map(FilmSession::getId).orElseThrow();
            int sessionIdSecond = filmSessions.stream().skip(1).findFirst().map(FilmSession::getId).orElseThrow();
            int sessionIdThird = filmSessions.stream().skip(2).findFirst().map(FilmSession::getId).orElseThrow();
            tickets = List.of(new Ticket(sessionIdFirst, 1, 1, 1),
                    new Ticket(sessionIdFirst, 2, 2, 1),
                    new Ticket(sessionIdSecond, 1, 1, 1),
                    new Ticket(sessionIdSecond, 2, 2, 1),
                    new Ticket(sessionIdThird, 1, 1, 1),
                    new Ticket(sessionIdThird, 2, 2, 1));
        }
    }

    @AfterAll
    static void clear() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM film_sessions WHERE id IN (:ids)")
                    .addParameter("ids", filmSessions.stream().map(FilmSession::getId).toList())
                    .executeUpdate();

            connection.createQuery("DELETE FROM halls WHERE id = :id")
                    .addParameter("id", hall.getId()).executeUpdate();

            connection.createQuery("DELETE FROM films WHERE id = :id")
                    .addParameter("id", film.getId()).executeUpdate();

            connection.createQuery("DELETE FROM files WHERE id = :id")
                    .addParameter("id", file.getId()).executeUpdate();

            connection.createQuery("DELETE FROM genres WHERE id = :id")
                    .addParameter("id", genre.getId()).executeUpdate();
        }
    }

    @AfterEach
    void clearTickets() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM tickets WHERE id IN (:ids)")
                    .addParameter("ids", tickets.stream().map(Ticket::getId).toList())
                    .executeUpdate();
        }
    }

    /**
     * Проверяет успешный сценарий сохранения данных билета методом {@code save}
     */
    @Test
    void whenSaveThenGetTicket() {
        var ticket = tickets.stream().findFirst().orElseThrow();

        var actualTicket = sql2oTicketRepository.save(ticket);

        assertThat(actualTicket.isPresent()).isTrue();
        assertThat(actualTicket.get()).isEqualTo(ticket);
    }

    /**
     * Проверяет неуспешный сценарий сохранения данных билета методом {@code save}
     */
    @Test
    void whenSaveExistedThenGetEmpty() {
        var ticket = tickets.stream().findFirst().orElseThrow();
        sql2oTicketRepository.save(ticket);

        assertThrows(Sql2oException.class, () -> sql2oTicketRepository.save(ticket));
    }

    /**
     * Проверяет сценарий получения данных билетов методом {@code findBySessionId}
     */
    @Test
    void whenFindBySessionIdThenGetTickets() {
        addTickets();
        int sessionId = tickets.stream().findFirst().map(Ticket::getSessionId).orElseThrow();
        var expectedTickets = tickets.stream().filter(ticket -> ticket.getSessionId() == sessionId).toList();

        var actualTickets = sql2oTicketRepository.findBySessionId(sessionId);

        assertThat(actualTickets).containsExactlyInAnyOrderElementsOf(expectedTickets);
    }

    private void addTickets() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("INSERT INTO tickets (session_id, row_number, place_number, user_id)"
                    + " VALUES (:session_id, :row_number, :place_number, :user_id)", true);
            for (Ticket ticket : tickets) {
                int generatedId = query.addParameter("session_id", ticket.getSessionId())
                        .addParameter("row_number", ticket.getRowNumber())
                        .addParameter("place_number", ticket.getPlaceNumber())
                        .addParameter("user_id", ticket.getUserId())
                        .executeUpdate().getKey(Integer.class);
                ticket.setId(generatedId);
            }
        }
    }

    /**
     * Проверяет сценарий получения данных билетов методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetTickets() {
        addTickets();

        var actualTickets = sql2oTicketRepository.findAll();

        assertThat(actualTickets).containsExactlyInAnyOrderElementsOf(tickets);
    }
}