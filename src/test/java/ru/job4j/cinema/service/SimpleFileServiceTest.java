package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.io.TempDir;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.file.FileRepository;
import ru.job4j.cinema.service.file.FileService;
import ru.job4j.cinema.service.file.SimpleFileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFileServiceTest {

    private FileRepository fileRepository;
    private FileService fileService;

    @BeforeEach
    public void init() {
        fileRepository = mock(FileRepository.class);
        fileService = new SimpleFileService(fileRepository);
    }

    /**
     * Проверяет успешный сценарий возврата данных файла по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetFileDTO(@TempDir Path tempDir) throws IOException {
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        var name = "test1";
        var path = tempDir.resolve(name);
        var content = new byte[] {1, 2, 3};
        when(fileRepository.findById(integerArgumentCaptor.capture()))
                .thenReturn(Optional.of(new File(name, path.toString())));
        Files.write(path, content);

        var actualFileDTO = fileService.findById(id);
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(actualFileDTO.isPresent()).isTrue();
        assertThat(actualFileDTO.get().content()).isEqualTo(content);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных файла по id методом {@code findById}
     */
    @Test
    void whenFindByIncorrectIdThenGetEmpty() {
        var id = 1;
        var integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileRepository.findById(integerArgumentCaptor.capture()))
                .thenReturn(Optional.empty());

        var actualFileDTO = fileService.findById(id);
        var actualId = integerArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(actualFileDTO.isEmpty()).isTrue();
    }
}