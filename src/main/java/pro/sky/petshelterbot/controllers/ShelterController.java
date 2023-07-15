package pro.sky.petshelterbot.controllers;

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

    @PostMapping(path = "/add")
    public ResponseEntity<Shelter> add(
            @RequestParam String name,
            @RequestParam String workTime,
            @RequestParam String address,
            @RequestParam String tel,
            @RequestParam String email,
            @RequestParam String type
    ) {
        Shelter shelter = new Shelter(name, workTime, address, tel, email, type);
        return ResponseEntity.ok(shelterService.add(shelter));
    }

    @GetMapping
    public ResponseEntity<Collection<Shelter>> findAll() {
        return ResponseEntity.ok(shelterService.findAll());
    }

    @DeleteMapping(path = "/{$id}")
    public ResponseEntity<Shelter> delete(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.delete(id));
    }

    @GetMapping(path = "/{$id}")
    public ResponseEntity<Shelter> get(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.get(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Shelter> update(
            @RequestParam Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String workTime,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String tel,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(shelterService.update(id, name, workTime, address, tel, email, type));
    }
}
