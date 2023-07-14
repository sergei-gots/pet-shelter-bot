package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.service.AdopterService;

import java.util.List;

@RestController
@RequestMapping(path = "/adopter")
@Tag(name = "AdopterController")
public class AdopterController {

    private final AdopterService adopterService;

    public AdopterController(AdopterService adopterService) {
        this.adopterService = adopterService;
    }

    @GetMapping(path = "/all-ready-to-adopt")
    public ResponseEntity<List<Adopter>> getAllReadyToAdopt(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(adopterService.getAllReadyToAdopt(pageNo, pageSize));
    }

}
