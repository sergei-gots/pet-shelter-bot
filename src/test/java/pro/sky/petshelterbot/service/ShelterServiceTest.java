package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.util.DataGenerator;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShelterServiceTest {

    @MockBean
    private ShelterRepository shelterRepository =  Mockito.mock(ShelterRepository.class);

    private final ShelterService shelterService = new ShelterService(shelterRepository);

    @Test
    void findAll() {
    }

    @Test
    void add_get_findAll() {
        Shelter shelter = DataGenerator.generateShelter();

        when(shelterRepository.save(any())).thenReturn(shelter);
        when(shelterRepository.getShelterById(anyLong())).thenReturn(shelter);
        when(shelterRepository.findAll()).thenReturn(List.of(shelter));

        Shelter actual = shelterService.add(shelter);

        assertThat(actual).isEqualTo(shelter);
        assertThat(actual).isIn(shelterService.findAll());
        assertThat(shelterService.findAll().size())
                .isEqualTo(1);

        Shelter actualGet = shelterService.get(actual.getId());
        assertThat(actualGet).isEqualTo(shelter);
    }

    @Test
    void delete() {
    }

    @Test
    void update() {
    }
}