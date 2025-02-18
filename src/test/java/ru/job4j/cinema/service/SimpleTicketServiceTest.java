package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.ticket.TicketRepository;
import ru.job4j.cinema.service.ticket.SimpleTicketService;
import ru.job4j.cinema.service.ticket.TicketService;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleTicketServiceTest {

    private TicketRepository ticketRepository;
    private TicketService ticketService;

    @BeforeEach
    public void init() {
        ticketRepository = mock(TicketRepository.class);
        ticketService = new SimpleTicketService(ticketRepository);
    }

    /**
     * Проверяет успешный сценарий сохранения билета методом {@code save}
     */
    @Test
    void whenSaveThenGetTicket() {
        var sessionId = 1;
        var tickets = IntStream.rangeClosed(1, 3).mapToObj(i ->
                new Ticket(sessionId, i, i * 2, i * 3)).toList();
        var ticket = new Ticket(sessionId, 4, 8, 12);

        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(ticketRepository.findBySessionId(integerArgumentCaptor.capture())).thenReturn(tickets);
        var ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        when(ticketRepository.save(ticketArgumentCaptor.capture())).thenReturn(Optional.of(ticket));

        var actualTicket = ticketService.save(ticket);
        var actualSessionId = integerArgumentCaptor.getValue();
        var actualTicketArgumentCaptor = ticketArgumentCaptor.getValue();

        assertThat(actualTicket.isPresent()).isTrue();
        assertThat(actualTicket.get()).isEqualTo(ticket);
        assertThat(actualSessionId).isEqualTo(sessionId);
        assertThat(actualTicketArgumentCaptor).isEqualTo(ticket);
    }

    /**
     * Проверяет неуспешный сценарий сохранения билета методом {@code save}
     */
    @Test
    void whenSaveOccupiedThenGetEmpty() {
        var sessionId = 1;
        var tickets = IntStream.rangeClosed(1, 3).mapToObj(i ->
                new Ticket(sessionId, i, i * 2, i * 3)).toList();
        var ticket = new Ticket(sessionId, 2, 4, 12);

        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(ticketRepository.findBySessionId(integerArgumentCaptor.capture())).thenReturn(tickets);

        var actualTicket = ticketService.save(ticket);
        var actualSessionId = integerArgumentCaptor.getValue();

        assertThat(actualTicket.isEmpty()).isTrue();
        assertThat(actualSessionId).isEqualTo(sessionId);
    }

    /**
     * Проверяет сценарий получения билетов по id сеанса методом {@code findBySessionId}
     */
    @Test
    void whenFindBySessionIdThenGetTickets() {
        var sessionId = 1;
        var tickets = IntStream.rangeClosed(1, 3).mapToObj(i ->
                new Ticket(sessionId, i, i * 2, i * 3)).toList();

        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(ticketRepository.findBySessionId(integerArgumentCaptor.capture())).thenReturn(tickets);

        var actualTickets = ticketService.findBySessionId(sessionId);
        var actualSessionId = integerArgumentCaptor.getValue();

        assertThat(actualTickets).containsExactlyInAnyOrderElementsOf(tickets);
        assertThat(actualSessionId).isEqualTo(sessionId);
    }

    /**
     * Проверяет сценарий получения карты соответствия количества билетов и id сеанса методом {@code numberOfTicketsForSessions}
     */
    @Test
    void numberOfTicketsForSessions() {
        var sessionId = 1;
        var tickets = IntStream.rangeClosed(1, 6).mapToObj(i -> {
            var currentSessionId = sessionId;
            if (i >= 4 && i <= 5) {
                currentSessionId *= 2;
            } else if (i > 5) {
                currentSessionId *= 3;
            }
            return new Ticket(currentSessionId, i, i * 2, i * 3);
        }).toList();
        var numberOfTickets = Map.of(sessionId, 3, sessionId * 2, 2, sessionId * 3, 1);

        when(ticketRepository.findAll()).thenReturn(tickets);

        var actualNumberOfTickets = ticketService.numberOfTicketsForSessions();

        assertThat(actualNumberOfTickets).containsExactlyInAnyOrderEntriesOf(numberOfTickets);
    }
}