package pro.sky.petshelterbot.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/reports")
@Tag(name = "ReportController")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{id}")
    @ApiResponse(description = "Получение отчёта по id")
    public ResponseEntity<Report> get(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.get(id));
    }

    @GetMapping(path = "/pet/{petId}")
    @ApiResponse(description = "Распечатывает в хронологическом порядке все отчёты по конкретному животному.")
    public ResponseEntity<List<Report>> getAllByPetId(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0", required = false) Integer pageNb,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        return ResponseEntity.ok(reportService.getAllByPetId(petId, pageNb, pageSize));
    }

    @GetMapping("/overdue/{shelterId}")
    @ApiResponse(description = "Возвращает животных на пробном периоде адоптации, для которых адоптеры не прислали текущие отчёты своевременно.")
    public ResponseEntity<List<Pet>> findOverdueReports(@PathVariable Long shelterId) {
        return ResponseEntity.ok(reportService.findOverdueReports(shelterId));
    }

    @GetMapping("/all/{shelterId}")
    @ApiResponse(description =
            "Распечатывает все имеющиеся в базе данных отчёты пользователей, сортированные по дате. " +
            "В рамках конкретного приюта.")
    public ResponseEntity<List<Report>> findAllReports(
            @PathVariable Long shelterId,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(reportService.getAllByShelterId(shelterId, pageNo, pageSize));
    }

    @PutMapping()
    @ApiResponse(description = "Обновляет страницу отчётов в базе данных. " +
            "Предполагается, что посылаемые отчёты могут иметь обновление в полях Report.checked и Report.approved. " +
            "При этом если approved == false, " +
            "то пользователю отсылается сообщение с рекомендацией" +
            "заполнять отчёты более полно.")
    public ResponseEntity<Report> updateReport(@RequestBody Report report) {
        return ResponseEntity.ok(reportService.update(report));
    }

    @GetMapping("/to-review/{shelterId}")
    @ApiResponse(description = "Распечатывает все отчёты пользователей, требующие проверки.")
    public ResponseEntity<List<Report>> findAllReportsToReview(
            @PathVariable Long shelterId,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(reportService.getAllReportsByShelterIdToReview(shelterId, pageNo, pageSize));
    }
}
