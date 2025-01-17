package ru.job4j.cinema.dto;

public record FilmDto(String name, String description, int year, int minimalAge, int durationInMinutes, String genre, String fileName, byte[] fileContent) {
}
