package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.user.Sql2oUserRepository;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void setUp() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    /**
     * Проверяет успешный сценарий сохранения данных пользователя методом {@code save}
     */
    @Test
    void whenSaveThenGetUser() {
        var user = new User("Test", "test@test.test", "testPassword");

        var isSaved = sql2oUserRepository.save(user).isPresent();
        var actualUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());

        assertThat(isSaved).isTrue();
        assertThat(actualUser.isPresent()).isTrue();
        assertThat(actualUser.get()).isEqualTo(user);
    }

    /**
     * Проверяет неуспешный сценарий сохранения данных пользователя методом {@code save}
     */
    @Test
    void whenSaveExistedThenGetEmpty() {
        var user = new User("Test", "test@test.test", "testPassword");

        sql2oUserRepository.save(user);
        var actualUser = sql2oUserRepository.save(user);

        assertThat(actualUser.isEmpty()).isTrue();
    }

    /**
     * Проверяет успешный сценарий получения данных пользователя методом {@code findByEmailAndPassword}
     */
    @Test
    void whenFindByEmailAndPasswordThenGetUser() {
        var user = new User("Test", "test@test.test", "testPassword");
        sql2oUserRepository.save(user);

        var actualUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());

        assertThat(actualUser.isPresent()).isTrue();
        assertThat(actualUser.get()).isEqualTo(user);
    }

    /**
     * Проверяет неуспешный сценарий получения данных пользователя методом {@code findByEmailAndPassword}
     */
    @Test
    void whenFindByEmailAndPasswordUnExistedThenGetEmpty() {
        var email = "test@test.test";
        var password = "testPassword";

        var actualUser = sql2oUserRepository.findByEmailAndPassword(email, password);

        assertThat(actualUser.isEmpty()).isTrue();
    }
}