package pro.sky.petshelterbot.controller;

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

    @GetMapping("/{adopterId}")
    public ResponseEntity<Adopter> getAdopter(@RequestParam Long adopterId) {
        return ResponseEntity.ok(adopterService.getAdopter(adopterId));
    }

    /* PUT /pet/setAdopter */
    @PutMapping("/setAdopter/{petId}")
    public ResponseEntity<Pet> setAdopter(@PathVariable Long petId, @RequestBody Adopter adopter) {
        return ResponseEntity.ok(adopterService.setAdopter(petId, adopter));
    }

    /* PUT /pet/prolongTrial14/{petId} */
    @PutMapping("/prolongTrial14/{petId}")
    public ResponseEntity<Pet> prolongTrial14(@PathVariable Long petId) {
        return ResponseEntity.ok(adopterService.prolongTrialForNDays(petId, 14));
    }

    /* /pet/prolongTrial30/{petId} */
    @PutMapping("prolongTrial30/{petId}")
    public ResponseEntity<Pet> prolongTrial30(@PathVariable Long petId) {
        return ResponseEntity.ok(adopterService.prolongTrialForNDays(petId, 30));
    }

    /* PUT /pet/cancelTrial/{petId} */
    @DeleteMapping("cancelTrial/{petId}")
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
