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
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.service.AdopterService;
import pro.sky.petshelterbot.util.DataGenerator;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = AdopterController.class)
class AdopterControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdopterService adopterService;


    private final String URL = "/adopters/";

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void get() throws Exception {
        Adopter adopter = DataGenerator.generateAdopter();
        when(adopterService.get(adopter.getChatId())).thenReturn(adopter);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL + "{id}", adopter.getChatId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Adopter actual = objectMapper.readValue(
                    mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                    Adopter.class
            );
            assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(actual)
                    .isNotNull()
                    .isEqualTo(adopter);
        });
    }

    /**
     * Test for @PutMapping
     * ResponseEntity<Pet>
     * setAdopter(@PathVariable Long petId, @RequestBody Adopter adopter);
     **/
    @Test
    public void setAdopterForPet() throws Exception {
        Shelter shelter = DataGenerator.generateShelter();
        Pet pet = DataGenerator.generatePet(shelter);
        Adopter adopter = DataGenerator.generateAdopter();
        when(adopterService.setAdopterForPet(pet.getId(), adopter)).thenReturn(pet);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URL + "/setAdopterForPet/{id}", pet.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(adopter))
                )
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse =
                            result.getResponse();
                    Pet actual = objectMapper.readValue(
                            mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8),
                            Pet.class
                    );
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(actual)
                            .isNotNull()
                            .isEqualTo(pet);
                });

    }

    /**
     * Test for @PutMapping("/prolongTrial14/{petId}")
     * ResponseEntity<Pet> prolongTrial14(@PathVariable Long petId)
     */
    @Test
    void prolongTrial14() throws Exception {
        Pet pet = DataGenerator.generatePet();
        long days = 14L;

        when(adopterService.prolongTrialForNDays(pet.getId(), days)).thenReturn(pet);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URL + "/prolongTrial14/{petId}", pet.getId())
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(result -> {
                            MockHttpServletResponse response = result.getResponse();
                            Pet actual = objectMapper.readValue(
                                    response.getContentAsString(StandardCharsets.UTF_8),
                                    Pet.class
                            );
                            assertThat(response.getStatus())
                                    .isEqualTo(HttpStatus.OK.value());
                            assertThat(actual)
                                    .isNotNull()
                                    .isEqualTo(pet);
                        }
                );
    }

    /**
     * Test for @PutMapping("/prolongTrial30/{petId}")
     * ResponseEntity<Pet> prolongTrial30(@PathVariable Long petId)
     */
    @Test
    void prolongTrial30() throws Exception {
        Pet pet = DataGenerator.generatePet();
        long days = 30L;

        when(adopterService.prolongTrialForNDays(pet.getId(), days)).thenReturn(pet);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(URL + "/prolongTrial30/{petId}", pet.getId())
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(result -> {
                            MockHttpServletResponse response = result.getResponse();
                            Pet actual = objectMapper.readValue(
                                    response.getContentAsString(StandardCharsets.UTF_8),
                                    Pet.class
                            );
                            assertThat(response.getStatus())
                                    .isEqualTo(HttpStatus.OK.value());
                            assertThat(actual)
                                    .isNotNull()
                                    .isEqualTo(pet);
                        }
                );
    }


    /**
     * Test @DeleteMapping("cancelTrial/{petId}")
     */
    @Test
    void cancelTrial() throws Exception {
        Pet pet = DataGenerator.generatePet();

        when(adopterService.cancelTrial(pet.getId())).thenReturn(pet);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "cancelTrial/{petId}", pet.getId())
                        .contentType(MediaType.APPLICATION_JSON
                        )


                )
                .andExpect(result -> {
                            MockHttpServletResponse response = result.getResponse();
                            assertThat(response.getStatus())
                                    .isEqualTo(HttpStatus.OK.value());
                            Pet actual = objectMapper.readValue(
                                    response.getContentAsString(StandardCharsets.UTF_8),
                                    Pet.class
                            );
                            assertThat(actual)
                                    .isNotNull()
                                    .isEqualTo(pet);
                        }
                );
    }

    /**
     * Test @GetMapping(path = "/all-ready-to-adopt")
     */
    @Test
    void getAllReadyToAdopt() throws Exception {
        Shelter shelter = DataGenerator.generateShelter();
        List<Adopter> adopters = Stream
                .generate(() -> DataGenerator.generateAdopter(shelter))
                .limit(DataGenerator.generateCount())
                .collect(Collectors.toList());

        when(adopterService.getAllReadyToAdoptByShelterId(shelter.getId(), 0, 10))
                .thenReturn(adopters);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + "/all-ready-to-adopt/{shelterId}", shelter.getId())
                        .contentType(MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(result -> {
                            MockHttpServletResponse response = result.getResponse();
                            assertThat(response.getStatus())
                                    .isEqualTo(HttpStatus.OK.value());
                            List<Adopter> actual = objectMapper.readValue(
                                    response.getContentAsString(StandardCharsets.UTF_8),
                                    new TypeReference<>() {
                                    }
                            );
                            assertThat(actual)
                                    .isNotNull()
                                    .containsExactlyInAnyOrderElementsOf(adopters);
                        }
                );
    }

}