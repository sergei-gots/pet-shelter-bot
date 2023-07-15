package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.service.AdopterService;
import pro.sky.petshelterbot.service.PetService;

import java.util.List;

@RestController
@RequestMapping(path = "/adopter")
@Tag(name = "AdopterController")
public class AdopterController {

    private final AdopterService adopterService;
    private final PetService petService;

    public AdopterController(AdopterService adopterService, PetService petService) {
        this.adopterService = adopterService;
        this.petService = petService;
    }

    /* PUT /pet/setAdopter */
    @PutMapping("/setAdopter")
    public ResponseEntity<Pet> setAdopter(@RequestBody Pet pet, @RequestBody Adopter adopter) {
        return ResponseEntity.ok(adopterService.setAdopter(pet, adopter));
    }

    /* PUT /pet/prolongTrial14/{petId} */
    @PutMapping("/prolongTrial14/{petId}")
    public ResponseEntity<Pet> prolongTrial14(@PathVariable Long petId) {
        return ResponseEntity.ok(adopterService.prolongTrial14(petId));
    }

    /* /pet/prolongTrial30/{petId} */
    @PutMapping("prolongTrial30/{petId}")
    public ResponseEntity<Pet> prolongTrial30(@PathVariable Long petId) {
        return ResponseEntity.ok(adopterService.prolongTrial30(petId));
    }

    /* PUT /pet/cancelTrial/{petId} */
    @PutMapping("cancelTrial/{petId}")
    public ResponseEntity<Pet> cancelTrial(@PathVariable Long petId) {
        return ResponseEntity.ok(adopterService.cancelTrial(petId));
    }

    @GetMapping(path = "/all-ready-to-adopt")
    public ResponseEntity<List<Adopter>> getAllReadyToAdopt(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(adopterService.getAllReadyToAdopt(pageNo, pageSize));
    }

}
