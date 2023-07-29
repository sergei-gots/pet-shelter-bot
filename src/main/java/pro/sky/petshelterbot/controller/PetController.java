package pro.sky.petshelterbot.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.service.PetService;

import java.util.List;

@RestController
@RequestMapping(path = "/pets")
@Tag(name = "PetController")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @ApiResponse(description =
            "Добавляет кота в кошачий шелтер/собаку в собачий шелтер. " +
            "Показывает сохраненные значения из БД и сообщает, " +
            "что данные о животном сохранены или не сохранены.")
    public ResponseEntity<Pet> add(@RequestBody Pet pet) {
        return ResponseEntity.ok(petService.add(pet));
    }

    @PostMapping(value = "/img/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(description = "Добавление изображения для животного.")
    public ResponseEntity<Pet> addImg(
            @PathVariable Long petId,
            @RequestPart MultipartFile img) {
        return ResponseEntity.ok(petService.addImg(petId, img));
    }

    @DeleteMapping()
    @ApiResponse(description = "Удаляет животного.")
    public ResponseEntity<Pet> delete(@RequestBody Pet pet) {
        return ResponseEntity.ok(petService.delete(pet));
    }

    /* GET /cat-shelter/pets
    GET /dog-shelter/pets */
    @GetMapping("/in-shelter/{shelterId}")
    @ApiResponse(description =
            "Распечатывает страницу из списка всех котов, " +
            "то есть как находящихся в приюте, так и адоптируемых или находящихся в адоптации. " +
            "Поиск в методах осуществляется по shelter_id.")
    public ResponseEntity<List<Pet>> getPetsByShelterId(
            @PathVariable Long shelterId,
            @RequestParam(defaultValue = "0", required = false)  Integer pageNo,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        return ResponseEntity.ok(petService.getByShelterId(shelterId, pageNo, pageSize));
    }

    @GetMapping("/{petId}")
    @ApiResponse(description = "Получение животного по id")
    public ResponseEntity<Pet> get(@PathVariable Long petId) {
        return ResponseEntity.ok(petService.get(petId));
    }

}
