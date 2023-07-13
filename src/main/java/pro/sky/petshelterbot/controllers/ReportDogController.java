package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.service.ReportService;

import java.util.List;

@RestController
@RequestMapping( "/dogs-cat")
@Tag(name = "Cat Reports API", description = "Cat report info.")
public class ReportDogController {

    private final ReportService reportService;

    public ReportDogController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<List<Report>> findAllByCatId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(reportService.findAllByDogId(id, pageNo, pageSize));
    }

    @PostMapping(path = "/{id}")
    public ResponseEntity<Report> update(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean checked,
            @RequestParam(required = false) Boolean approved) {
        return ResponseEntity.ok(reportService.updateReportDog(id, checked, approved));
    }

}
