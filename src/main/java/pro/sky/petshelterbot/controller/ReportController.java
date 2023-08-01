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
    public ResponseEntity<List<Pet>> getOverdueReports(@PathVariable Long shelterId) {
        return ResponseEntity.ok(reportService.getOverdueReports(shelterId));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a page from the list of all the reports within the shelter specified with shelter ID." +
                            "Sorted by date.",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                                   /* ToDo
                                   ,
                                       examples = @ExampleObject(
                                            description = "Page then there is the only report in the shelter",
                                            value = "classpath:swagger-doc/non-checked-reports.json"
                                    )*/
                            )
                    }
            )
    })
    @GetMapping("/shelter/{shelterId}")
    public ResponseEntity<List<Report>> getAllByShelterId(
            @Parameter(description="Shelter ID", required = true, example = "1")
                @NotNull @PathVariable Long shelterId,
            @Parameter(description="Page number")
                @RequestParam(defaultValue = "0", required = false)  Integer pageNb,
            @Parameter(description="Number of got entries within the page")
            @RequestParam(defaultValue = "10", required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(reportService.getAllByShelterId(shelterId, pageNb, pageSize));
    }

    @Operation(
            summary =  "Marks the report as checked and approved.",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The report checked and approved.",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                                    /* ToDo
                                    ,
                                    examples = @ExampleObject(
                                            description = "Example of a returned checked report marked as approved",
                                            externalValue = "file://src/main/resources/swagger-doc/approved-report.json"
                                    )

                                     */
                            )
                    }
            )
    })
    @PutMapping("/approve/{id}")
    @ApiResponse(description = "Mark the report as approved")
    public ResponseEntity<Report> approve(
            @Parameter(description="Report ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(reportService.approve(id));
    }


    @Operation(summary = "Marks the report as filled inappropriately.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Report marked as filled inappropriately.",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                            ,

                                            examples = @ExampleObject(
                                                    description = "Example of a returned report marked as disapproved",
                                                    externalValue = "file://src/main/resources/swagger-doc/disapproved-report.json"
                                            )
                                    )
                            }
                    )
            })
    @PutMapping("/disapprove/{id}")
    public ResponseEntity<Report> disapprove(
            @Parameter(description="Report ID", required = true, example = "1")
                @PathVariable Long id) {
        return ResponseEntity.ok(reportService.disapprove(id));
    }

    @PutMapping()
    @ApiResponse(description = "Обновляет страницу отчётов в базе данных. " +
            "Предполагается, что посылаемые отчёты могут иметь обновление в полях Report.checked и Report.approved. " +
            "При этом если approved == false, " +
            "то пользователю отсылается сообщение с рекомендацией" +
            "заполнять отчёты более полно.")
    public ResponseEntity<Report> update(@RequestBody Report report) {
        return ResponseEntity.ok(reportService.update(report));
    }

    @Operation(
            summary = "Returns a page from the list of all the reports within the shelter specified with shelter ID " +
                    "which are unchecked.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of the list of all the reports within the shelter specified with shelter ID " +
                                    "which are unchecked.",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = Report.class)),
                                            examples = @ExampleObject(
                                                    description = "Example of a page then there is the only report in the shelter",
                                                    externalValue = "file://src/main/resources/swagger-doc/non-checked-reports.json"
                                            )
                                    )
                            }
                    )
            })
    @GetMapping("/to-review/{shelterId}")
    public ResponseEntity<List<Report>> getAllReportsToReview(
            @Parameter(description="Shelter ID", required = true, example = "1")
                @NotNull @PathVariable Long shelterId,
            @Parameter(description="Page number")
                @RequestParam(defaultValue = "0", required = false)  Integer pageNb,
            @Parameter(description="Number of got entries within the page")
                @RequestParam(defaultValue = "10", required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(reportService.getAllReportsByShelterIdToReview(shelterId, pageNb, pageSize));
    }


}
