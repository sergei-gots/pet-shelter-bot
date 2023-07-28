package pro.sky.petshelterbot.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.service.AdopterService;

import java.util.List;

@RestController
@RequestMapping(path = "/adopters")
@Tag(name = "AdopterController")
public class AdopterController {

    private final AdopterService adopterService;

    public AdopterController(AdopterService adopterService) {
        this.adopterService = adopterService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Adopter> getAdopter(@PathVariable Long id) {
        return ResponseEntity.ok(adopterService.getAdopter(id));
    }

    /* PUT /pet/setAdopter */
    @PutMapping("/setAdopterForPet/{petId}")
    public ResponseEntity<Pet> setAdopterForPet(@PathVariable Long petId, @RequestBody Adopter adopter) {
        return ResponseEntity.ok(adopterService.setAdopterForPet(petId, adopter));
    }

    /* PUT /pet/prolongTrial14/{petId} */
    @PutMapping("/prolongTrial14/{petId}")
    public ResponseEntity<Pet> prolongTrial14(@PathVariable Long petId) {
        return ResponseEntity.ok(adopterService.prolongTrialForNDays(petId, 14L));
    }

    /* /pet/prolongTrial30/{petId} */
    @PutMapping("prolongTrial30/{petId}")
    public ResponseEntity<Pet> prolongTrial30(@PathVariable Long petId) {
        return ResponseEntity.ok(adopterService.prolongTrialForNDays(petId, 30L));
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
