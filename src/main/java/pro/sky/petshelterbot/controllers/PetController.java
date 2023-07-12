package pro.sky.petshelterbot.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.service.PetService;

import java.util.Collection;

@RestController("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<Pet> add(@RequestBody Pet pet) {
        return ResponseEntity.ok(petService.add(pet));
    }

    @PostMapping(path = "/add")
    public ResponseEntity<Pet> add(@RequestParam String species, @RequestParam String name, @RequestParam Shelter shelter) {
        return ResponseEntity.ok(petService.add(species, name, shelter));
    }

    @GetMapping(path = "/delete/{$id}")
    public ResponseEntity<Pet> delete(@PathVariable Long id) {
        return ResponseEntity.ok(petService.delete(id));
    }

    @GetMapping(path = "/get/{$id}")
    public ResponseEntity<Pet> get(@PathVariable Long id) {
        return ResponseEntity.ok(petService.get(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Pet>> findAll() {
        return ResponseEntity.ok(petService.findAll());
    }

}
