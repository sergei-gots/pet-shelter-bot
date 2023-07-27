package pro.sky.petshelterbot.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.service.ShelterService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/shelters")
@Tag(name = "ShelterController")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @PostMapping
    public ResponseEntity<Shelter> add(@RequestBody Shelter shelter) {
        return ResponseEntity.ok(shelterService.add(shelter));
    }

    @GetMapping
    public ResponseEntity<Collection<Shelter>> findAll() {
        return ResponseEntity.ok(shelterService.findAll());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Shelter> delete(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.delete(id));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Shelter> get(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.get(id));
    }

    @PutMapping
    public ResponseEntity<Shelter> update(@RequestBody Shelter shelter) {
        return ResponseEntity.ok(shelterService.update(shelter));
    }
}
