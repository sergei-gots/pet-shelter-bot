package pro.sky.petshelterbot.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.petshelterbot.entity.Adopter;
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

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a page from the list of the pets within the shelter specified with shelter ID",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class)),
                                    examples = @ExampleObject(
                                            description = "Example of a page then there is the only pet in the shelter",
                                            externalValue = "file://src/main/resources/swagger-doc/pets-in-shelter.json"
                                    )
                            )
                    }
            )
    })
    @GetMapping("/in-shelter/{shelterId}")
    public ResponseEntity<List<Pet>> getPetsByShelterId(
            @Parameter(description="Shelter ID", required = true, example = "1")
                @NotNull @PathVariable Long shelterId,
            @Parameter(description="Page number")
                @RequestParam(defaultValue = "0", required = false)  Integer pageNb,
            @Parameter(description="Number of got entries within the page")
                @RequestParam(defaultValue = "10", required = false) Integer pageSize) {

        return ResponseEntity.ok(petService.getByShelterId(shelterId, pageNb, pageSize));

    }

    @GetMapping("/{petId}")
    @ApiResponse(description = "Получение животного по id")
    public ResponseEntity<Pet> get(@PathVariable Long petId) {
        return ResponseEntity.ok(petService.get(petId));
    }

}
