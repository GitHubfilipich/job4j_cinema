package ru.job4j.cinema.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.file.Sql2oFileRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oFileRepositoryTest {

    private static Sql2oFileRepository sql2oFileRepository;
    private static File file;
    private static Sql2o sql2o;

    @BeforeAll
    public static void setUp() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oFileRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        file = new File("test", "test");
    }

    @AfterEach
    public void clearFile() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM files WHERE id = :id")
                    .addParameter("id", file.getId()).executeUpdate();
        }
    }

    /**
     * Проверяет успешный сценарий возврата данных файла по id методом {@code findById}
     */
    @Test
    public void whenFindByIdThenGetFile() {
        addFile();

        var actualResult = sql2oFileRepository.findById(file.getId());

        assertThat(actualResult).isEqualTo(Optional.of(file));
    }

    private void addFile() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("INSERT INTO files (name, path) VALUES (:name, :path)", true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            file.setId(generatedId);
        }
    }

    /**
     * Проверяет неуспешный сценарий возврата данных файла по id методом {@code findById}
     */
    @Test
    public void whenFindByInCorrectIdThenGetEmpty() {
        var actualResult = sql2oFileRepository.findById(file.getId());

        assertThat(actualResult).isEqualTo(Optional.empty());
    }
}