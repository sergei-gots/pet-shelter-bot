package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.service.ReportService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping( "/reports-cat")
@Tag(name = "Cat Reports API", description = "Cat report info.")
public class ReportCatController {

    private final ReportService reportService;

    public ReportCatController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<List<Report>> findAllByCatId(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.findAllByCatId(id));
    }



}
