package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.service.DogShelterService;

@RestController
@RequestMapping(path = "/dog-shelter")
@Tag(name = "DogShelterController")
public class DogShelterController {

    private final DogShelterService dogShelterService;

    public DogShelterController(DogShelterService dogShelterService) {
        this.dogShelterService = dogShelterService;
    }

    /* POST /cat-shelter/add
    POST /dog-shelter/add */
    @PostMapping("/add")
    @ApiResponse(description = "" +
            "Добавляет кота в кошачий шелтер/собаку в собачий шелтер. " +
            "Показывает сохраненные значения из БД и сообщает, " +
            "что данные о животном сохранены или не сохранены.")
    public ResponseEntity<Pet> add(@RequestBody Pet pet) {
        return ResponseEntity.ok(dogShelterService.createDog(pet));
    }

    /* GET /cat-shelter/pets
    GET /dog-shelter/pets */
    // ---------------------------------------

    /* POST /cat-shelter/volunteers/
    POST /dog-shelter/volunteers/ */


}
