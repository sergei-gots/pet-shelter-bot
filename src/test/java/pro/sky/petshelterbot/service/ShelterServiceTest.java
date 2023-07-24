package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.sky.petshelterbot.PetShelterBotApplication;
import pro.sky.petshelterbot.entity.Shelter;


import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= PetShelterBotApplication.class)
class ShelterServiceTest {


    @Autowired
    private ShelterService shelterService;

    @Test
    void findAll() {
    }

    @Test
    void add() {
        int beforeCount = shelterService.findAll().size();
        Shelter shelter = new Shelter("animal shelter",
                "24/7", "M2121 West",  "77271112233", "test@email.org",
                "test_shelter_type");

        shelterService.add(shelter);
        Shelter actual = shelterService.get(shelter.getId());
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(shelter);
                assertThat(actual)
                        .usingRecursiveComparison()
                        .isIn(shelterService.findAll());
        assertThat(shelterService.findAll().size()).isEqualTo(beforeCount+1);
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