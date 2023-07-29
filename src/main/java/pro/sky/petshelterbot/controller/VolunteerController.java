package pro.sky.petshelterbot.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.service.VolunteerService;

import java.util.List;

@RestController
@RequestMapping("/volunteers")
@Tag(name = "VolunteerController")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @PostMapping
    @ApiResponse(description = "Добавляет волонтёра в список волонтёров шелтера")
    public ResponseEntity<Volunteer> add(@RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(volunteerService.add(volunteer));
    }

    @GetMapping("/{id}")
    @ApiResponse(description = "Возвращает список всех волонтёров шелтера")
    public ResponseEntity<Volunteer> get(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.get(id));
    }

    @GetMapping("/shelter/{shelterId}")
    @ApiResponse(description = "Возвращает список всех волонтёров шелтера")
    public ResponseEntity<List<Volunteer>> getAllByShelterId(@PathVariable Long shelterId) {
        return ResponseEntity.ok(volunteerService.getAllVolunteersByShelterId(shelterId));
    }


    @DeleteMapping
    @ApiResponse(description = "Удаляет волонтёра из списка волонтёров шелтера.")
    public ResponseEntity<Volunteer> deleteVolunteer(@RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(volunteerService.delete(volunteer));
    }
}
