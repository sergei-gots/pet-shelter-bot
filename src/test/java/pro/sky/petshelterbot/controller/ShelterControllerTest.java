package pro.sky.petshelterbot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.service.ShelterService;
import pro.sky.petshelterbot.util.DataGenerator;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers =  ShelterController.class)
class ShelterControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShelterService shelterService;


    private final String URL = "/shelters/";
    private final String URL_WITH_ID = "/shelters/{id}";

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void add() throws Exception {

        Shelter shelter = DataGenerator.generateShelter();
        when(shelterService.add(shelter)).thenReturn(shelter);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shelter)
                        )
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Shelter actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Shelter.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual)
                    .isNotNull()
                    .isEqualTo(shelter);
        });
    }

    @Test
    void findAll() throws Exception {
        Collection<Shelter> expected = Stream
                .generate(DataGenerator::generateShelter)
                .limit(2)
                .collect(Collectors.toList());

        when(shelterService.findAll()).thenReturn(expected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Collection<Shelter> actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    new TypeReference<>() {
                    }
            );
            assertThat(actual)
                    .isNotNull()
                    .hasSize(expected.size())
                    .containsExactlyInAnyOrderElementsOf(expected);
        });
    }

    @Test
    void delete() throws Exception {

        Shelter shelter = DataGenerator.generateShelter();
        when(shelterService.delete(shelter.getId())).thenReturn(shelter);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(URL_WITH_ID, shelter.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Shelter actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Shelter.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual)
                    .isNotNull()
                    .isEqualTo(shelter);
        });
    }

    @Test
    void get() throws Exception {
        Shelter shelter = DataGenerator.generateShelter();
        when(shelterService.get(shelter.getId())).thenReturn(shelter);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL_WITH_ID, shelter.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Shelter actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Shelter.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual)
                    .isNotNull()
                    .isEqualTo(shelter);
        });
    }

    @Test
    void update() throws Exception {
        Shelter shelter = DataGenerator.generateShelter();
        when(shelterService.update(shelter)).thenReturn(shelter);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shelter)
                        )
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Shelter actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Shelter.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual)
                    .isNotNull()
                    .isEqualTo(shelter);
        });
    }


}