package pro.sky.petshelterbot.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.util.DataGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShelterControllerTest {

    @LocalServerPort
    private int port;

    private String sheltersUrl() {
        return "http://localhost:" + port + "/shelters";
    }

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ShelterRepository shelterRepository;



    @AfterEach
    public void afterEach() {
        shelterRepository.deleteAll();
    }

    @Test
    void add() {
        Shelter shelter = DataGenerator.generateShelter();
        ResponseEntity<Shelter> addResponseEntity =
                testRestTemplate.exchange(
                        sheltersUrl(),
                        HttpMethod.PUT,
                        new HttpEntity<>(shelter),
                        Shelter.class);
        assertThat(addResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Shelter putShelter = addResponseEntity.getBody();
        assertThat(putShelter).isNotNull();
        assertThat(putShelter).isEqualTo(shelter);
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