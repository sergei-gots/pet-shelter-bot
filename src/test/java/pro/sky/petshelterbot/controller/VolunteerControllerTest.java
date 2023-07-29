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
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.service.VolunteerService;
import pro.sky.petshelterbot.util.DataGenerator;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = VolunteerController.class)
class VolunteerControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerService volunteerService;


    private final String URL = "/volunteers/";

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void get() throws Exception {
        Shelter shelter = DataGenerator.generateShelter();
        Volunteer volunteer = DataGenerator.generateVolunteer(shelter);
        when(volunteerService.get(volunteer.getChatId())).thenReturn(volunteer);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL + "{id}", volunteer.getChatId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Volunteer actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Volunteer.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual)
                    .isNotNull()
                    .isEqualTo(volunteer);
        });
    }

    @Test
    void getAllByShelterId() throws Exception {
        Shelter shelter = DataGenerator.generateShelter();
        List<Volunteer> volunteers = Stream
                .generate(() -> DataGenerator.generateVolunteer(shelter))
                .limit(DataGenerator.generateCount())
                .collect(Collectors.toList());

        when(volunteerService.getAllVolunteersByShelterId(shelter.getId()))
                .thenReturn(volunteers);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + "/shelter/{shelterId}", shelter.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(result -> {
                            MockHttpServletResponse response = result.getResponse();
                            assertThat(response.getStatus())
                                    .isEqualTo(HttpStatus.OK.value());
                            List<Volunteer> actual = objectMapper.readValue(
                                    response.getContentAsString(StandardCharsets.UTF_8),
                                    new TypeReference<>() {
                                    }
                            );
                            assertThat(actual)
                                    .isNotNull()
                                    .containsExactlyInAnyOrderElementsOf(volunteers);
                        }
                );
    }

    @Test
    void add() throws Exception {

        Volunteer volunteer = DataGenerator.generateVolunteer();
        when(volunteerService.add(volunteer)).thenReturn(volunteer);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(volunteer)
                        )
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Volunteer actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Volunteer.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual)
                    .isNotNull()
                    .isEqualTo(volunteer);
        });
    }

    @Test
    void delete()  throws Exception {

        Volunteer volunteer = DataGenerator.generateVolunteer();
        when(volunteerService.delete(volunteer)).thenReturn(volunteer);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(URL, volunteer.getChatId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(volunteer))
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Volunteer actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Volunteer.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual)
                    .isNotNull()
                    .isEqualTo(volunteer);
        });
    }
}