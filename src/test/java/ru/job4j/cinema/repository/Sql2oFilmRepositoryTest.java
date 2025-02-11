package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oFilmRepositoryTest {

    private static Sql2oFilmRepository sql2oFilmRepository;
    private static Sql2o sql2o;
    private static Genre genre;
    private static File file;
    private static Collection<Film> films;

    @BeforeAll
    static void setUp() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        file = new File("TestName", "TestPath");
        genre = new Genre("testGenre");
        films = List.of(
                new Film("TestFilm1", "TestDescription1", 2020, genre.getId(),
                        1, 60, file.getId()),
                new Film("TestFilm2", "TestDescription2", 2021, genre.getId(),
                        2, 90, file.getId()),
                new Film("TestFilm3", "TestDescription3", 2022, genre.getId(),
                        3, 120, file.getId()));
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("INSERT INTO files (name, path) VALUES (:name, :path)", true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            file.setId(generatedId);

            query = connection.createQuery("INSERT INTO genres (name) VALUES (:name)", true)
                    .addParameter("name", genre.getName());
            generatedId = query.executeUpdate().getKey(Integer.class);
            genre.setId(generatedId);
        }
    }

    @AfterAll
    static void clear() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM files WHERE id = :id")
                    .addParameter("id", file.getId()).executeUpdate();

            connection.createQuery("DELETE FROM genres WHERE id = :id")
                    .addParameter("id", genre.getId()).executeUpdate();
        }
    }

    @AfterEach
    void clearFilms() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM films WHERE id IN (:ids)")
                    .addParameter("ids", films.stream().map(Film::getId).toList())
                    .executeUpdate();
        }
    }

    /**
     * Проверяет сценарий возврата данных всех фильмов методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetFilms() {
        addFilms();

        var actualFilms = sql2oFilmRepository.findAll();

        assertThat(actualFilms).containsAll(films);
    }

    private void addFilms() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("INSERT INTO films (name, description, \"year\", genre_id, "
                    + "minimal_age, duration_in_minutes, file_id) VALUES (:name, :description, :year, :genre_id, "
                    + " :minimal_age, :duration_in_minutes, :file_id)", true);
            for (Film film : films) {
                int generatedId = query.addParameter("name", film.getName())
                        .addParameter("description", film.getDescription())
                        .addParameter("year", film.getYear())
                        .addParameter("genre_id", genre.getId())
                        .addParameter("minimal_age", film.getMinimalAge())
                        .addParameter("duration_in_minutes", film.getDurationInMinutes())
                        .addParameter("file_id", file.getId())
                        .executeUpdate().getKey(Integer.class);
                film.setId(generatedId);
            }
        }
    }

    /**
     * Проверяет успешный сценарий возврата данных фильма по id методом {@code findById}
     */
    @Test
    void whenFindByCorrectIdThenGetFilm() {
        addFilms();
        var film = films.stream().findFirst().get();

        var actualFilm = sql2oFilmRepository.findById(film.getId());

        assertThat(actualFilm.isPresent()).isTrue();
        assertThat(actualFilm.get()).isEqualTo(film);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных фильма по id методом {@code findById}
     */
    @Test
    void whenFindByInCorrectIdThenGetEmpty() {
        var id = -1;

        var actualFilm = sql2oFilmRepository.findById(id);

        assertThat(actualFilm.isEmpty()).isTrue();
    }
}