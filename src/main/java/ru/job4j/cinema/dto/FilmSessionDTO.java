package ru.job4j.cinema.dto;

import java.time.LocalDateTime;

public record FilmSessionDTO(int id, LocalDateTime startTime, LocalDateTime endTime, String hall, String film, int price, long vacant) {
}
