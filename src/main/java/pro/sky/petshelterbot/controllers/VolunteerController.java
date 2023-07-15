package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.service.PetService;
import pro.sky.petshelterbot.service.VolunteerService;

import java.util.List;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final PetService petService;

    public VolunteerController(VolunteerService volunteerService, PetService petService) {
        this.volunteerService = volunteerService;
        this.petService = petService;
    }

    /* POST /cat-shelter/volunteers/
    POST /dog-shelter/volunteers/ */
    @PostMapping
    @ApiResponse(description = "Добавляет волонтёра в список волонтёров шелтера")
    public ResponseEntity<Volunteer> AddVolunteerToShelter(@RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(petService.AddVolunteerToShelter(volunteer));
    }

    /* GET /dog-shelter/volunteers/ */
    @GetMapping("/{shelterId}")
    @ApiResponse(description = "Возвращает список всех волонтёров шелтера")
    public ResponseEntity<List<Volunteer>> findAllVolunteersByShelterId(@PathVariable Long shelterId) {
        return ResponseEntity.ok(petService.findAllVolunteersByShelterId(shelterId));
    }

    /* DELETE /dog-shelter/volunteers/ */
    @DeleteMapping
    @ApiResponse(description = "Удаляет волонтёра из списка волонтёров шелтера.")
    public ResponseEntity<Volunteer> deleteVolunteer(@RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(volunteerService.deleteVolunteer(volunteer));
    }
}
