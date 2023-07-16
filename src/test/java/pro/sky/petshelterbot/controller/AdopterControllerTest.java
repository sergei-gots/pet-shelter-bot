package pro.sky.petshelterbot.controller;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdopterControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private final Faker faker = new Faker();

    private Shelter generateShelter() {
        Shelter shelter = new Shelter();
        shelter.setName("Doggi");
        shelter.setType("dog");
        shelter.setTel("+1 123 321 123");
        shelter.setAddress("7 ave. st");
        shelter.setEmail("info@doggi.com");
        shelter.setWorkTime("Mon-Sun: 09:00AM - 10:00PM");
        return shelter;
    }

    private Adopter generateAdopter() {
        Adopter adopter = new Adopter();
        adopter.setChatId(faker.random().nextLong());
        adopter.setFirstName(faker.funnyName().name());
        return adopter;
    }

    private Pet generatePet(Shelter shelter) {
        Pet pet = new Pet();
        pet.setName(faker.dog().name());
        pet.setSpecies("dog");
        pet.setShelter(shelter);
        pet.setDisabled(false);
        return pet;
    }

    private Pet addPet(Pet pet) {
        ResponseEntity<Pet> petResponseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/pets",
                pet,
                Pet.class
        );
        assertThat(petResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(petResponseEntity.getBody()).isNotNull();
        assertThat(petResponseEntity.getBody()).usingRecursiveComparison()
                .ignoringFields("id").isEqualTo(pet);
        assertThat(petResponseEntity.getBody().getId()).isNotNull();

        return petResponseEntity.getBody();
    }

    private Shelter addShelter(Shelter shelter) {
        ResponseEntity<Shelter> shelterResponseEntity = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/shelters",
                shelter,
                Shelter.class
        );
        assertThat(shelterResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(shelterResponseEntity.getBody()).isNotNull();
        assertThat(shelterResponseEntity.getBody()).usingRecursiveComparison()
                .ignoringFields("id").isEqualTo(shelter);
        assertThat(shelterResponseEntity.getBody().getId()).isNotNull();

        return shelterResponseEntity.getBody();
    }

    @Test
    public void setAdopterTest() {
        Shelter shelter = addShelter(generateShelter());
        Pet pet = addPet(generatePet(shelter));
        System.out.println(pet);
        System.out.println(shelter);
        ResponseEntity<Pet> getForEntityResponse = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/pets/" + pet.getId(),
                Pet.class
        );
        assertThat(getForEntityResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getForEntityResponse.getBody()).isNotNull();
        assertThat(getForEntityResponse.getBody()).usingRecursiveComparison().isEqualTo(pet);
        assertThat(getForEntityResponse.getBody().getShelter()).usingRecursiveComparison()
                .isEqualTo(shelter);
    }
}
