package ru.job4j.cinema.service.implementation;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;
import ru.job4j.cinema.service.HallService;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimpleHallService implements HallService {

    private final HallRepository hallRepository;

    public SimpleHallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @Override
    public Optional<Hall> findById(int id) {
        return hallRepository.findById(id);
    }

    @Override
    public Collection<Hall> findAll() {
        return hallRepository.findAll();
    }
}
