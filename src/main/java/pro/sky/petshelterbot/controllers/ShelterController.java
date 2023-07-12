package pro.sky.petshelterbot.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.service.ShelterService;

import java.util.Collection;

@RestController("/shelters")
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

    @GetMapping(path = "/delete/{$id}")
    public ResponseEntity<Shelter> delete(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.delete(id));
    }

    @GetMapping(path = "/{$id}")
    public ResponseEntity<Shelter> get(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.get(id));
    }

}
