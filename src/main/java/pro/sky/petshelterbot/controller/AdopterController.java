package pro.sky.petshelterbot.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;
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
        return ResponseEntity.ok(adopterService.get(id));
    }

    @Operation(summary = "Assigns the adopter for a pet specified with petID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class),
                                            examples = @ExampleObject(
                                                    description = "Example of a Pet is being adopted",
                                                    externalValue = "file://src/main/resources/swagger-doc/pet.json"
                                            )
                                    )
                            }
                    )
            })
    @PutMapping("/setAdopterForPet/{petId}")
    public ResponseEntity<Pet> setAdopterForPet(
            @Parameter(description = "Pet ID", required = true, example = "1")
            @NotNull @PathVariable Long petId,
            @Parameter(description = "Adopter", required = true, examples = @ExampleObject(
                    externalValue = "file://src/main/resources/swagger-doc/adopter.json"))
            @RequestBody Adopter adopter) {
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
    @PutMapping("cancelTrial/{petId}")
    public ResponseEntity<Pet> cancelTrial(@PathVariable Long petId) {
        return ResponseEntity.ok(adopterService.cancelTrial(petId));
    }


    @Operation(summary = "Returns a page from the list of all the adopters within the shelter specified with shelter ID " +
            "which are ready to adopt a pet.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page from the list of all the adopters within the shelter specified with shelter ID " +
                                    "which are ready to adopt a pet.",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array =@ArraySchema(schema = @Schema(implementation = Adopter.class)),
                                            examples = @ExampleObject(
                                                    description = "Example of a page then there is the only adopter ready to adopt within the shelter",
                                                    externalValue = "file://src/main/resources/swagger-doc/adopters.json"
                                            )
                                    )
                            }
                    )
            })
    @GetMapping(path = "/all-ready-to-adopt/{shelterId}")
    public ResponseEntity<List<Adopter>> getAllReadyToAdopt(
            @Parameter(description = "Shelter ID", required = true, example = "1")
            @NotNull @PathVariable Long shelterId,
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0", required = false) Integer pageNb,
            @Parameter(description = "Number of entries within the page")
            @RequestParam(defaultValue = "10", required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(adopterService.getAllReadyToAdoptByShelterId(shelterId, pageNb, pageSize));
    }

}
