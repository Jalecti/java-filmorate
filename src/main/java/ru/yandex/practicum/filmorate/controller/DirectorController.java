package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Director> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{directorId}")
    public Director getDirectorById(@PathVariable Long directorId) {
        return directorService.findDirectorBy(directorId);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director directorRequest) {
        return directorService.create(directorRequest);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director directorRequest) {
        return directorService.update(directorRequest);
    }

    @DeleteMapping("/{directorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Valid @PathVariable("directorId") Long directorId) {
        directorService.delete(directorId);
    }
}
