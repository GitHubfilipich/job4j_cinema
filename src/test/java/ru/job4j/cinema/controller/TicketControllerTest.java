package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TicketControllerTest {

    private FilmSessionService filmSessionService;
    private TicketService ticketService;
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        filmSessionService = mock(FilmSessionService.class);
        ticketService = mock(TicketService.class);
        ticketController = new TicketController(filmSessionService, ticketService);
    }

    /**
     * Проверяет успешный сценарий возврата страницы покупки билета методом {@code getTicketPage}
     */
    @Test
    void whenGetTicketPageByCorrectIdThenGetPageWithSession() {
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        var session = new FilmSessionDTO(1, LocalDateTime.of(2025, 2, 1, 10, 0),
                LocalDateTime.of(2025, 2, 1, 11, 0),
                "Test1", "Test1", 100, 1, 1, 2);
        when(filmSessionService.findById(integerArgumentCaptor.capture())).thenReturn(Optional.of(session));
        var model = new ConcurrentModel();

        var actualResponse = ticketController.getTicketPage(model, id);
        var actualId = integerArgumentCaptor.getValue();
        var actualSession = model.getAttribute("currentSession");

        assertThat(actualResponse).isEqualTo("/tickets/purchase");
        assertThat(actualId).isEqualTo(id);
        assertThat(actualSession).isEqualTo(session);
    }

    /**
     * Проверяет неуспешный сценарий возврата страницы покупки билета методом {@code getTicketPage}
     */
    @Test
    void whenGetTicketPageByInCorrectIdThenGetErrorPage() {
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(filmSessionService.findById(integerArgumentCaptor.capture())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var actualResponse = ticketController.getTicketPage(model, id);
        var actualId = integerArgumentCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(actualResponse).isEqualTo("errors/404");
        assertThat(actualId).isEqualTo(id);
        assertThat(actualMessage).isEqualTo("Сеанс с указанным идентификатором не найден");
    }

    /**
     * Проверяет успешный сценарий покупки билета методом {@code buyTicket}
     */
    @Test
    void whenBuyTicketByCorrectTicketThenGetSuccessPage() {
        var ticket = new Ticket(1, 2, 3, 4);
        var ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        when(ticketService.save(ticketArgumentCaptor.capture())).thenReturn(Optional.of(ticket));
        var model = new ConcurrentModel();
        var message = String.format("Вы успешно приобрели билет на ряд %s место %s.",
                ticket.getRowNumber(), ticket.getPlaceNumber());

        var actualResponse = ticketController.buyTicket(ticket, model);
        var actualTicket = ticketArgumentCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(actualResponse).isEqualTo("/tickets/purchaseSuccess");
        assertThat(actualTicket).isEqualTo(ticket);
        assertThat(actualMessage).isEqualTo(message);
    }

    /**
     * Проверяет неуспешный сценарий покупки билета методом {@code buyTicket}
     * когда выбранное место занято
     */
    @Test
    void whenBuyTicketByInCorrectTicketThenGetUnSuccessPage() {
        var ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        when(ticketService.save(ticketArgumentCaptor.capture())).thenReturn(Optional.empty());
        var ticket = new Ticket(1, 2, 3, 4);
        var model = new ConcurrentModel();
        var message = "Не удалось приобрести билет на заданное место. "
                + "Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.";

        var actualResponse = ticketController.buyTicket(ticket, model);
        var actualTicket = ticketArgumentCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(actualResponse).isEqualTo("/tickets/purchaseUnSuccess");
        assertThat(actualTicket).isEqualTo(ticket);
        assertThat(actualMessage).isEqualTo(message);
    }

    /**
     * Проверяет неуспешный сценарий покупки билета методом {@code buyTicket}
     * когда в процессе покупки происходит ошибка
     */
    @Test
    void whenBuyTicketAndThrowExceptionThenGetErrorPage() {
        var expectedException = new RuntimeException("Test");
        when(ticketService.save(any())).thenThrow(expectedException);
        var model = new ConcurrentModel();

        var actualResponse = ticketController.buyTicket(new Ticket(), model);
        var actualMessage = model.getAttribute("message");

        assertThat(actualResponse).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedException.getMessage());
    }
}