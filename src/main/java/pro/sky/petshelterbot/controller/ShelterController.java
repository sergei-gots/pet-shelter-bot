package pro.sky.petshelterbot.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.service.ShelterService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/shelters")
@Tag(name = "ShelterController")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @PostMapping
    public ResponseEntity<Shelter> add(@RequestBody Shelter shelter) {
        return ResponseEntity.ok(shelterService.add(shelter));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of shelters within the pet-shelter-bot app's aggregating db ",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter[].class),
                                    examples = @ExampleObject(
                                            name = "List of shelters",
                                            value = "[\n\t{\n"+
                                                    "\t\"id\": 3,\n"+
                                                    "\t\"name\": \"Crocodile Shelter\",\n"+
                                            "\t\"workTime\": \"24/7\",\n"+
                                            "\t\"address\": \"3, Pelikaanstaat\",\n"+
                                            "\t\"tel\": \"8-800-CROCODILE\",\n"+
                                            "\t\"email\": \"crocodile@shelters.org\",\n"+
                                            "\t\"type\": \"CROCODILE\"\n"+
                                            "\t}\n]"
                                    )
                            )
                    }
            )
    })
    @GetMapping
    public ResponseEntity<Collection<Shelter>> getAll() {
        return ResponseEntity.ok(shelterService.getAll());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Shelter> delete(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.delete(id));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Shelter> get(@PathVariable Long id) {
        return ResponseEntity.ok(shelterService.get(id));
    }

    @PutMapping
    public ResponseEntity<Shelter> update(@RequestBody Shelter shelter) {
        return ResponseEntity.ok(shelterService.update(shelter));
    }
}
