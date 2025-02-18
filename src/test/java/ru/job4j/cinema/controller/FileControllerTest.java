package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.file.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    public void initServices() {
        this.fileService = mock(FileService.class);
        this.fileController = new FileController(fileService);
    }

    /**
     * Проверяет успешный сценарий возврата данных файла по id методом {@code findById}
     */
    @Test
    void whenGetByCorrectIdThenGetResponseEntityOk() {
        var id = 1;
        var content = new byte[]{1, 2, 3};
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileService.findById(integerArgumentCaptor.capture()))
                .thenReturn(Optional.of(new FileDto("Test", content)));

        var expectedResponse = ResponseEntity.ok(content);

        var actualResponse = fileController.getById(id);
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных файла по id методом {@code findById}
     */
    @Test
    void whenGetByInCorrectIdThenGetResponseEntityNotFound() {
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileService.findById(integerArgumentCaptor.capture()))
                .thenReturn(Optional.empty());

        var expectedResponse = ResponseEntity.notFound().build();

        var actualResponse = fileController.getById(id);
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}