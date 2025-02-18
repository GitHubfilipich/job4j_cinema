package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.hall.HallRepository;
import ru.job4j.cinema.service.hall.HallService;
import ru.job4j.cinema.service.hall.SimpleHallService;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleHallServiceTest {

    private HallRepository hallRepository;
    private HallService hallService;

    @BeforeEach
    public void init() {
        hallRepository = mock(HallRepository.class);
        hallService = new SimpleHallService(hallRepository);
    }

    /**
     * Проверяет успешный сценарий возврата зала методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetHall() {
        var id = 1;
        var hall = new Hall("TestHall", 5, 6, "TestDescription");
        hall.setId(id);
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(hallRepository.findById(integerArgumentCaptor.capture())).thenReturn(Optional.of(hall));

        var actualHall = hallService.findById(hall.getId());
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualHall.isPresent()).isTrue();
        assertThat(actualHall.get()).isEqualTo(hall);
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий возврата зала методом {@code findById}
     */
    @Test
    void whenFindByInCorrectIdThenGetEmpty() {
        var id = 1;
        when(hallRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        var actualHall = hallService.findById(id);

        assertThat(actualHall.isEmpty()).isTrue();
    }

    /**
     * Проверяет сценарий возврата данных всех залов методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetHalls() {
        var halls = IntStream.rangeClosed(1, 3).mapToObj(i -> {
            var hall = new Hall("TestHall" + i, i * 5, i * 6, "TestDescription" + i);
            hall.setId(i);
            return hall;
        }).toList();
        when(hallRepository.findAll()).thenReturn(halls);

        var actualHalls = hallService.findAll();

        assertThat(actualHalls).containsExactlyInAnyOrderElementsOf(halls);
    }
}