package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.hall.Sql2oHallRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oHallRepositoryTest {

    private static Sql2oHallRepository sql2oHallRepository;
    private static Collection<Hall> halls;
    private static Sql2o sql2o;

    @BeforeAll
    public static void setUp() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oHallRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oHallRepository = new Sql2oHallRepository(sql2o);
        halls = List.of(new Hall("test1", 10, 20, "test1"),
                new Hall("test2", 12, 22, "test2"),
                new Hall("test3", 14, 24, "test2"));
    }

    @AfterEach
    public void clearGenre() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM halls WHERE id IN (:ids)")
                    .addParameter("ids", halls.stream().map(Hall::getId).toList())
                    .executeUpdate();
        }
    }

    /**
     * Проверяет успешный сценарий возврата данных зала по id методом {@code findById}
     */
    @Test
    void whenFindByCorrectIdThenGetHall() {
        addHalls();
        var hall = halls.stream().findFirst().orElseThrow();

        var actualHall = sql2oHallRepository.findById(hall.getId());

        assertThat(actualHall.isPresent()).isTrue();
        assertThat(actualHall.get()).isEqualTo(hall);
    }

    private void addHalls() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("INSERT INTO halls (name, row_count, place_count, description) "
                    + "VALUES (:name, :row_count, :place_count, :description)", true);
            for (Hall hall : halls) {
                int generatedId = query.addParameter("name", hall.getName())
                        .addParameter("row_count", hall.getRowCount())
                        .addParameter("place_count", hall.getPlaceCount())
                        .addParameter("description", hall.getDescription())
                        .executeUpdate().getKey(Integer.class);
                hall.setId(generatedId);
            }
        }
    }

    /**
     * Проверяет неуспешный сценарий возврата данных зала по id методом {@code findById}
     */
    @Test
    void whenFindByInCorrectIdThenGetEmpty() {
        var id = -1;

        var actualHall = sql2oHallRepository.findById(id);

        assertThat(actualHall.isEmpty()).isTrue();
    }

    /**
     * Проверяет сценарий возврата данных всех залов методом {@code findAll}
     */    @Test
    void whenFindAllThenGetHalls() {
        addHalls();

        var actualHall = sql2oHallRepository.findAll();

        assertThat(actualHall).containsAll(halls);
    }
}