package pro.sky.petshelterbot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.service.ReportService;
import pro.sky.petshelterbot.util.DataGenerator;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ReportController.class)
class ReportControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;


    private final String URL = "/reports/";

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void get() throws Exception {
        Pet pet = DataGenerator.generatePet();
        Report report = DataGenerator.generateReport(pet);
        when(reportService.get(report.getId())).thenReturn(report);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL + "{id}", report.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Report actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Report.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual)
                    .isNotNull()
                    .isEqualTo(report);
        });
    }

    @Test
    void getAllByPetId() throws Exception {
        Pet pet = DataGenerator.generatePet();
        int nReportsCount = DataGenerator.generateCount();
        int pageNb = 1;
        int pageSize = nReportsCount>>1;
        ArrayList<Report> reports = Stream
                .generate(() -> DataGenerator.generateReport(pet))
                .limit(nReportsCount)
                .collect(
                        Collectors.toCollection(ArrayList::new));
        List<Report> expected = reports.subList(pageSize, pageSize*2);

        when(reportService.getAllByPetId(pet.getId(), pageNb, pageSize))
                .thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + "/pet/{petId}", pet.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(result -> {
                            MockHttpServletResponse response = result.getResponse();
                            assertThat(response.getStatus())
                                    .isEqualTo(HttpStatus.OK.value());
                            List<Report> actual = objectMapper.readValue(
                                    response.getContentAsString(StandardCharsets.UTF_8),
                                    new TypeReference<>() {
                                    }
                            );
                            assertThat(actual)
                                    .isNotNull()
                                    .containsExactlyInAnyOrderElementsOf(expected);
                        }
                );
    }


}