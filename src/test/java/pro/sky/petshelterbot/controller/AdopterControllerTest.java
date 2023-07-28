package pro.sky.petshelterbot.controller;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers =  AdopterController.class)
class AdopterControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdopterService adopterService;


    private final String URL = "/adopters/";
    private final String URL_WITH_ID = "/adopters/{id}";

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void get() throws Exception {
        Adopter adopter = DataGenerator.generateAdopter();
        when(adopterService.getAdopter(adopter.getChatId())).thenReturn(adopter);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL_WITH_ID, adopter.getChatId().toString())
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

    /** Test for @PutMapping 
    * ResponseEntity<Pet> 
    *       setAdopter(@PathVariable Long petId, @RequestBody Adopter adopter);
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

    /** Test for @PutMapping("/prolongTrial14/{petId}")
     *  ResponseEntity<Pet> prolongTrial14(@PathVariable Long petId) 
     */
    @Test
    void prolongTrial14() {
        
    }

    /** Test for @PutMapping("/prolongTrial30/{petId}")
     *  ResponseEntity<Pet> prolongTrial30(@PathVariable Long petId) 
     */
    @Test
    void prolongTrial30() {
    }

    
    /**  Test @DeleteMapping("cancelTrial/{petId}")
     */
    @Test
    void cancelTrial() {
        
    }

    /** Test @GetMapping(path = "/all-ready-to-adopt") */
    @Test
    void getAllReadyToAdopt() {
        
    }
    
}