package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;
import ru.job4j.cinema.service.implementation.SimpleUserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleUserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        userService = new SimpleUserService(userRepository);
    }

    /**
     * Проверяет успешный сценарий сохранения пользователя методом {@code save}
     */
    @Test
    void whenSaveThenGetUser() {
        var user = new User("TestUser", "TestEmail", "TestPassword");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var actualUser = userService.save(user);
        var actualUserArgumentCaptor = userArgumentCaptor.getValue();

        assertThat(actualUser.isPresent()).isTrue();
        assertThat(actualUser.get()).isEqualTo(user);
        assertThat(actualUserArgumentCaptor).isEqualTo(user);
    }

    /**
     * Проверяет неуспешный сценарий сохранения пользователя методом {@code save}
     */
    @Test
    void whenSaveExistedThenGetEmpty() {
        var user = new User("TestUser", "TestEmail", "TestPassword");
        when(userRepository.save(any(User.class))).thenReturn(Optional.empty());

        var actualUser = userService.save(user);

        assertThat(actualUser.isEmpty()).isTrue();
    }

    /**
     * Проверяет успешный сценарий получения пользователя методом {@code findByEmailAndPassword}
     */
    @Test
    void whenFindByEmailAndPasswordThenGetUser() {
        var email = "TestEmail";
        var password = "TestPassword";
        var user = new User("TestUser", email, password);
        var emailArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var passwordArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(userRepository
                .findByEmailAndPassword(emailArgumentCaptor.capture(), passwordArgumentCaptor.capture()))
                .thenReturn(Optional.of(user));

        var actualUser = userService.findByEmailAndPassword(email, password);
        var actualEmail = emailArgumentCaptor.getValue();
        var actualPassword = passwordArgumentCaptor.getValue();

        assertThat(actualUser.isPresent()).isTrue();
        assertThat(actualUser.get()).isEqualTo(user);
        assertThat(actualEmail).isEqualTo(email);
        assertThat(actualPassword).isEqualTo(password);
    }

    /**
     * Проверяет неуспешный сценарий получения пользователя методом {@code findByEmailAndPassword}
     */
    @Test
    void whenFindByNonExistedEmailAndPasswordThenGetEmpty() {
        var email = "TestEmail";
        var password = "TestPassword";
        when(userRepository
                .findByEmailAndPassword(any(String.class), any(String.class)))
                .thenReturn(Optional.empty());

        var actualUser = userService.findByEmailAndPassword(email, password);

        assertThat(actualUser.isEmpty()).isTrue();
    }
}