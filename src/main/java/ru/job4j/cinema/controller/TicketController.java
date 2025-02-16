package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final FilmSessionService filmSessionService;
    private final TicketService ticketService;

    public TicketController(FilmSessionService filmSessionService, TicketService ticketService) {
        this.filmSessionService = filmSessionService;
        this.ticketService = ticketService;
    }

    @GetMapping("/purchase/{id}")
    public String getTicketPage(Model model, @PathVariable int id) {
        var filmSessionOptional = filmSessionService.findById(id);
        if (filmSessionOptional.isEmpty()) {
            model.addAttribute("message", "Сеанс с указанным идентификатором не найден");
            return "errors/404";
        }
        model.addAttribute("currentSession", filmSessionOptional.get());
        return "/tickets/purchase";
    }

    @PostMapping("/purchase")
    public String buyTicket(@ModelAttribute Ticket ticket, Model model) {
        var ticketOptional = ticketService.save(ticket);
        if (ticketOptional.isEmpty()) {
            model.addAttribute("message", "Не удалось приобрести билет на заданное место. "
                    + "Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
            return "/tickets/purchaseUnSuccess";
        }
        model.addAttribute("message",
                String.format("Вы успешно приобрели билет на ряд %s место %s.", ticket.getRowNumber(), ticket.getPlaceNumber()));
        return "/tickets/purchaseSuccess";
    }
}
