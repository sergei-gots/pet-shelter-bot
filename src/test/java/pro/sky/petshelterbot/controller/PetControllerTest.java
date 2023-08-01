package pro.sky.petshelterbot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.service.PetService;
import pro.sky.petshelterbot.util.DataGenerator;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers =  PetController.class)
class PetControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    private final String URL = "/pets/";


    @Autowired
    ObjectMapper objectMapper;

    @Test
    void add() throws Exception {

        Pet pet = DataGenerator.generatePet();
        when(petService.add(pet)).thenReturn(pet);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)
                        )
        ).andExpect(result -> {
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


    /** Test for
     * public ResponseEntity<List<Pet>> getShelterPets(
     *             @PathVariable Long shelterId,
     *             @RequestParam(defaultValue = "0") Integer pageNo,
     *             @RequestParam(defaultValue = "10") Integer pageSize)
     *
     */
    @Test
    public void getShelterPets() throws Exception  {

        int nPetsCount = DataGenerator.generateCount();
        Integer pageNb = 1;
        Integer pageSize = nPetsCount/2;
        Shelter shelter = DataGenerator.generateShelter();
        ArrayList<Pet> pets = Stream
                .generate(() -> DataGenerator.generatePet(shelter))
                .limit(nPetsCount)
                .collect(
                        Collectors.toCollection(
                                ArrayList::new)
                );
        List<Pet> expected = pets.subList(0, (pets.size()<10)? pets.size() : 9);//pageSize, pageSize * 2);
        when(petService.getByShelterId(
                shelter.getId(),
                pageNb, pageSize)).thenReturn(expected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL + "/in-shelter/{shelterId}", shelter.getId())
                        .queryParam("pageNb", pageNb.toString())
                        .queryParam("pageSize", pageSize.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Collection<Pet> actual = objectMapper.readValue(
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
    public void getShelterPetsWithDefaultPageNbAndSize() throws Exception  {

        int nPetsCount = DataGenerator.generateCount();
        int defaultPageNb = 0;
        int defaultPageSize = 10;
        Shelter shelter = DataGenerator.generateShelter();
        ArrayList<Pet> pets = Stream
                .generate(() -> DataGenerator.generatePet(shelter))
                .limit(nPetsCount)
                .collect(
                        Collectors.toCollection(
                                ArrayList::new)
                );

        List<Pet> expected = pets.subList(0,
                Math.min(pets.size(), defaultPageSize));
        when(petService.getByShelterId(
                shelter.getId(),
                defaultPageNb, defaultPageSize)).thenReturn(expected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL + "/in-shelter/{shelterId}", shelter.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(result -> {
            MockHttpServletResponse mockHttpServletResponse =
                    result.getResponse();
            Collection<Pet> actual = objectMapper.readValue(
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

        Pet pet = DataGenerator.generatePet();
        when(petService.delete(pet)).thenReturn(pet);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(URL, pet.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet))
        ).andExpect(result -> {
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

    @Test
    void get() throws Exception {
        Pet pet = DataGenerator.generatePet();
        when(petService.get(pet.getId())).thenReturn(pet);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL + "{petId}", pet.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(result -> {
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

    @Test
    void addImg() throws Exception {
        Pet pet = DataGenerator.generatePet();
        MockMultipartFile file
                = new MockMultipartFile(
                "img",
                "test.bin",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new byte[10]
        );

        when(petService.addImg(pet.getId(), file)).thenReturn(pet);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .multipart(HttpMethod.POST, URL + "img/{petId}", pet.getId().toString())
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        ).andExpect(result -> {
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

}