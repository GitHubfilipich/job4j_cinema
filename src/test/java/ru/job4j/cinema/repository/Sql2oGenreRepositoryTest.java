package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oGenreRepositoryTest {

    private static Sql2oGenreRepository sql2oGenreRepository;
    private static Collection<Genre> genres;
    private static Sql2o sql2o;

    @BeforeAll
    public static void setUp() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oGenreRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        genres = List.of(new Genre("test1"),
                new Genre("test2"),
                new Genre("test3"));
    }

    @AfterEach
    public void clearGenre() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM genres WHERE id IN (:ids)")
                    .addParameter("ids", genres.stream().map(Genre::getId).toList())
                    .executeUpdate();
        }
    }

    /**
     * Проверяет сценарий возврата данных всех жанров методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetGenres() {
        addGenres();

        var actualGenres = sql2oGenreRepository.findAll();

        assertThat(actualGenres).containsAll(genres);
    }

    private void addGenres() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("INSERT INTO genres (name) VALUES (:name)", true);
            for (Genre genre : genres) {
                int generatedId = query.addParameter("name", genre.getName())
                        .executeUpdate().getKey(Integer.class);
                genre.setId(generatedId);
            }
        }
    }

    /**
     * Проверяет успешный сценарий возврата данных жанра по id методом {@code findById}
     */
    @Test
    void whenFindByCorrectIdThenGetGenre() {
        addGenres();
        var genre = genres.stream().findFirst().get();

        var actualGenre = sql2oGenreRepository.findById(genre.getId());

        assertThat(actualGenre.isPresent()).isTrue();
        assertThat(actualGenre.get()).isEqualTo(genre);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных жанра по id методом {@code findById}
     */
    @Test
    void whenFindByInCorrectIdThenGetEmpty() {
        var id = -1;

        var actualGenre = sql2oGenreRepository.findById(id);

        assertThat(actualGenre.isEmpty()).isTrue();
    }
}