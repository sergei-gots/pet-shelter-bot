package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.service.PetService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/cats")
@Tag(name = "Cat pet API", description = "Cats info.")
public class PetCatController {

    private final PetService petService;

    public PetCatController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<Pet> add(@RequestParam String name) {
        return ResponseEntity.ok(petService.createCat(name));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Pet> delete(@PathVariable Long id) {
        return ResponseEntity.ok(petService.delete(id));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Pet> get(@PathVariable Long id) {
        return ResponseEntity.ok(petService.get(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Pet>> findAll() {
        return ResponseEntity.ok(petService.findAll());
    }

}
