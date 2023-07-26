package pro.sky.petshelterbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers =  ShelterController.class)
@ExtendWith(MockitoExtension.class)
class ShelterControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private final ShelterService shelterService = Mockito.mock(ShelterService.class) ;


    private final ShelterController shelterController = new ShelterController(shelterService);

    private final String url = "/shelter";

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void add() throws Exception {

        Shelter shelter = DataGenerator.generateShelter();
        when(shelterService.add((shelter))).thenReturn(shelter);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shelter)
                        )
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Shelter shelterResult = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Shelter.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(shelterResult)
                    .isNotNull()
                    .isEqualTo(shelter);
        });
    }

    @Test
    void findAll() {
    }

    @Test
    void delete() {
    }

    @Test
    void get() {
    }

    @Test
    void update() {
    }


}