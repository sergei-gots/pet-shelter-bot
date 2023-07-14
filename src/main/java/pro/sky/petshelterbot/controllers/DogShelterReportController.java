package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.service.DogShelterReportService;

import java.util.List;

@RestController
@RequestMapping( "/dog-shelter/reports")
@Tag(name = "DogShelterReportController")
public class DogShelterReportController {

    private final DogShelterReportService dogShelterReportService;

    public DogShelterReportController(DogShelterReportService dogShelterReportService) {
        this.dogShelterReportService = dogShelterReportService;
    }

    @GetMapping(path = "/{id}")
    @ApiResponse(description="Распечатывает в хронологическом порядке все отчёты по конкретному животному.")
    public ResponseEntity<List<Report>> findAllReportsByPetId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(dogShelterReportService.findAllByPetId(id, pageNo, pageSize));
    }

    @GetMapping("/overdue")
    @ApiResponse(description="Возвращает животных на пробном периоде адоптации, для которых адоптеры не прислали текущие отчёты своевременно.")
    public ResponseEntity<List<Pet>> findOverdueReports() {
        return ResponseEntity.ok(dogShelterReportService.findOverdueReports());
    }

    /*@PostMapping(path = "/{id}")
    public ResponseEntity<Report> update(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean checked,
            @RequestParam(required = false) Boolean approved) {
        return ResponseEntity.ok(dogShelterReportService.updateReportDog(id, checked, approved));
    }*/

}
