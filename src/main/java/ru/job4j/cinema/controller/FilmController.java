package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDTO;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;

import java.util.Comparator;

@Controller
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final FilmSessionService filmSessionService;

    public FilmController(FilmService filmService, FilmSessionService filmSessionService) {
        this.filmService = filmService;
        this.filmSessionService = filmSessionService;
    }

    @GetMapping("/list")
    public String getAll(Model model) {
        var films = filmService.findAll()
                .stream()
                .sorted(Comparator.comparing(FilmDto::name, Comparator.naturalOrder()))
                .toList();
        model.addAttribute("films", films);
        return "films/list";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var filmOptional = filmService.findById(id);
        if (filmOptional.isEmpty()) {
            model.addAttribute("message", "Фильм с указанным идентификатором не найден");
            return "errors/404";
        }
        model.addAttribute("film", filmOptional.get());
        return "films/film";
    }

    @GetMapping("/sessions")
    public String getSessions(Model model) {
        var sessions = filmSessionService.findAll()
                .stream()
                .sorted(Comparator.comparing(FilmSessionDTO::startTime, Comparator.naturalOrder()))
                .toList();
        model.addAttribute("sessions", sessions);
        return "films/sessions";
    }

    @GetMapping("/sessions/buy/{id}")
    public String buyTicket(@PathVariable int id) {
        return "redirect:/tickets/purchase/" + id;
    }

}
