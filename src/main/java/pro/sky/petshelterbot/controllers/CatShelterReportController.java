package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.service.DogShelterReportService;

import java.util.List;

@RestController
@RequestMapping( "/cat-shelter/reports")
@Tag(name = "CatShelterReportController")
public class CatShelterReportController {



}
