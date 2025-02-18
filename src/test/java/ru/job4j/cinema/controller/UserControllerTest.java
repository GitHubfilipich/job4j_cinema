package ru.job4j.cinema.controller;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        session = mock(HttpSession.class);
    }

    /**
     * Проверяет сценарий возврата страницы регистрации методом {@code getRegistrationPage}
     */
    @Test
    void whenGetRegistrationPageThenGetPage() {
        var user = new User("Test1", "test@test.test", "test1");
        var stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(session.getAttribute(stringArgumentCaptor.capture())).thenReturn(user);
        var model = new ConcurrentModel();

        var actualResponse = userController.getRegistrationPage(model, session);
        var actualAttribute = stringArgumentCaptor.getValue();
        var actualUser = model.getAttribute("user");

        assertThat(actualResponse).isEqualTo("users/register");
        assertThat(actualAttribute).isEqualTo("user");
        assertThat(actualUser).isEqualTo(user);
    }

    /**
     * Проверяет успешный сценарий регистрации методом {@code register}
     */
    @Test
    void whenRegisterSuccessfulThenGetSessionsPage() {
        var user = new User("Test1", "test@test.test", "test1");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));
        var stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var userSessionArgumentCaptor = ArgumentCaptor.forClass(User.class);
        doNothing().when(session).setAttribute(stringArgumentCaptor.capture(), userSessionArgumentCaptor.capture());
        var model = new ConcurrentModel();

        var actualResponse = userController.register(model, user, session);
        var actualUser = userArgumentCaptor.getValue();
        var actualAttribute = stringArgumentCaptor.getValue();
        var actualUserSession = userSessionArgumentCaptor.getValue();

        assertThat(actualResponse).isEqualTo("redirect:/films/sessions");
        assertThat(actualUser).isEqualTo(user);
        assertThat(actualAttribute).isEqualTo("user");
        assertThat(actualUserSession).isEqualTo(user);
    }

    /**
     * Проверяет неуспешный сценарий регистрации методом {@code register}
     */
    @Test
    void whenRegisterUnSuccessfulThenGetErrorMessage() {
        var user = new User("Test1", "test@test.test", "test1");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var actualResponse = userController.register(model, user, session);
        var actualUser = userArgumentCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(actualResponse).isEqualTo("users/register");
        assertThat(actualUser).isEqualTo(user);
        assertThat(actualMessage).isEqualTo("Пользователь с такой почтой уже существует");
    }

    /**
     * Проверяет сценарий возврата страницы входа методом {@code getLoginPage}
     */
    @Test
    void whenGetLoginPageThenGetPage() {
        var actualResponse = userController.getLoginPage();

        assertThat(actualResponse).isEqualTo("users/login");
    }

    /**
     * Проверяет успешный сценарий входа методом {@code loginUser}
     */
    @Test
    void whenLoginUserSuccessfulThenGetSessionsPage() {
        var user = new User("Test1", "test@test.test", "test1");
        var stringArgumentCaptorEmail = ArgumentCaptor.forClass(String.class);
        var stringArgumentCaptorPassword = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(stringArgumentCaptorEmail.capture(),
                stringArgumentCaptorPassword.capture())).thenReturn(Optional.of(user));
        var stringArgumentCaptorSession = ArgumentCaptor.forClass(String.class);
        var userArgumentCaptorSession = ArgumentCaptor.forClass(User.class);
        doNothing().when(session).setAttribute(stringArgumentCaptorSession.capture(),
                userArgumentCaptorSession.capture());
        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);

        var actualResponse = userController.loginUser(user, model, request);
        var actualEmail = stringArgumentCaptorEmail.getValue();
        var actualPassword = stringArgumentCaptorPassword.getValue();
        var actualSessionAttribute = stringArgumentCaptorSession.getValue();
        var actualSessionUser = userArgumentCaptorSession.getValue();

        assertThat(actualResponse).isEqualTo("redirect:/films/sessions");
        assertThat(actualEmail).isEqualTo(user.getEmail());
        assertThat(actualPassword).isEqualTo(user.getPassword());
        assertThat(actualSessionAttribute).isEqualTo("user");
        assertThat(actualSessionUser).isEqualTo(user);
    }

    /**
     * Проверяет неуспешный сценарий входа методом {@code loginUser}
     */
    @Test
    void whenLoginUserUnSuccessfulThenGetErrorPage() {
        var user = new User("Test1", "test@test.test", "test1");
        var stringArgumentCaptorEmail = ArgumentCaptor.forClass(String.class);
        var stringArgumentCaptorPassword = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(stringArgumentCaptorEmail.capture(),
                stringArgumentCaptorPassword.capture())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var actualResponse = userController.loginUser(user, model, new Request(new Connector()));
        var actualEmail = stringArgumentCaptorEmail.getValue();
        var actualPassword = stringArgumentCaptorPassword.getValue();
        var actualMessage = model.getAttribute("error");

        assertThat(actualResponse).isEqualTo("users/login");
        assertThat(actualEmail).isEqualTo(user.getEmail());
        assertThat(actualPassword).isEqualTo(user.getPassword());
        assertThat(actualMessage).isEqualTo("Почта или пароль введены неверно");
    }

    /**
     * Проверяет сценарий выхода методом {@code logout}
     */
    @Test
    void whenLogoutThenGetPageAndInvalidate() {
        var actualResponse = userController.logout(session);

        assertDoesNotThrow(() -> verify(session).invalidate());
        assertThat(actualResponse).isEqualTo("redirect:/");
    }
}