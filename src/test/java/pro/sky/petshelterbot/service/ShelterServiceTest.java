package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.ShelterRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ShelterServiceTest {


    private ShelterService shelterService = new ShelterService(new ShelterRepository())

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
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(shelter).ignoringFields("id")
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