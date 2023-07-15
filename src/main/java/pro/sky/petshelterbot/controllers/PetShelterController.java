package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.service.PetShelterService;

import java.util.List;

@RestController
@RequestMapping(path = "/shelter")
@Tag(name = "PetShelterController")
public class PetShelterController {

    private final PetShelterService petShelterService;

    public PetShelterController(PetShelterService petShelterService) {
        this.petShelterService = petShelterService;
    }

    /* POST /cat-shelter/add
    POST /dog-shelter/add */
    @PostMapping("/add")
    @ApiResponse(description = "" +
            "Добавляет кота в кошачий шелтер/собаку в собачий шелтер. " +
            "Показывает сохраненные значения из БД и сообщает, " +
            "что данные о животном сохранены или не сохранены.")
    public ResponseEntity<Pet> add(@RequestBody Pet pet) {
        return ResponseEntity.ok(petShelterService.add(pet));
    }

    @DeleteMapping()
    @ApiResponse(description = "Удаляет животного.")
    public ResponseEntity<Pet> delete(@RequestBody Pet pet) {
        return ResponseEntity.ok(petShelterService.delete(pet));
    }

    /* GET /cat-shelter/pets
    GET /dog-shelter/pets */
    @GetMapping("/pets/{shelterId}")
    @ApiResponse(description = "" +
            "Распечатывает страницу из списка всех котов, " +
            "то есть как находящихся в приюте, так и адоптируемых или находящихся в адоптации. " +
            "Поиск в методах осуществляется по shelter_id.")
    public ResponseEntity<List<Pet>> getAllPets(
            @PathVariable Long shelterId,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(petShelterService.findAllPets(shelterId, pageNo, pageSize));
    }

    /* POST /cat-shelter/volunteers/
    POST /dog-shelter/volunteers/ */
    @PostMapping("/volunteers")
    @ApiResponse(description = "Добавляет волонтёра в список волонтёров шелтера")
    public ResponseEntity<Volunteer> AddVolunteerToShelter(@RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(petShelterService.AddVolunteerToShelter(volunteer));
    }

    /* GET /dog-shelter/volunteers/ */
    @GetMapping("/volunteers/{shelterId}")
    @ApiResponse(description = "Возвращает список всех волонтёров шелтера")
    public ResponseEntity<List<Volunteer>> findAllVolunteersByShelterId(@PathVariable Long shelterId) {
        return ResponseEntity.ok(petShelterService.findAllVolunteersByShelterId(shelterId));
    }

    /* DELETE /dog-shelter/volunteers/ */
    @DeleteMapping("/volunteers")
    @ApiResponse(description="Удаляет волонтёра из списка волонтёров шелтера.")
    public ResponseEntity<Volunteer> deleteVolunteer(@RequestBody Volunteer volunteer) {
        return ResponseEntity.ok(petShelterService.deleteVolunteer(volunteer));
    }

}
