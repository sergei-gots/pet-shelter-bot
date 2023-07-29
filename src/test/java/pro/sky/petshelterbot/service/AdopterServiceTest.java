package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.repository.AdopterRepository;
import pro.sky.petshelterbot.repository.PetRepository;
import pro.sky.petshelterbot.util.DataGenerator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdopterServiceTest {

    @MockBean
    private AdopterRepository adopterRepository =  Mockito.mock(AdopterRepository.class);

    @MockBean
    private PetRepository petRepository = Mockito.mock(PetRepository.class);

    private final AdopterService adopterService =
            new AdopterService(adopterRepository, petRepository);

    @Test
    void get() {

        Adopter adopter = DataGenerator.generateAdopter();
        long chatId = adopter.getChatId();

        when(adopterRepository.getByChatId(chatId))
                .thenReturn(adopter);

        Adopter actual = adopterService.get(chatId);
        assertThat(actual)
                .isNotNull()
                .isEqualTo(adopter);
    }

    @Test
    void getAllReadyToAdoptByShelterId() {

        int count = DataGenerator.generateCount();
        Shelter shelter = DataGenerator.generateShelter();

        List<Adopter> adopters = Stream
                .generate(()->DataGenerator.generateAdopter(shelter))
                .limit(count)
                .collect(Collectors.toList());
        Page<Adopter> page = new PageImpl<>(adopters);

        when(adopterRepository.getAllReadyToAdoptByShelterId(any(Long.class),
                any(Pageable.class)))
                .thenReturn(page);

        List<Adopter> actual = adopterService.getAllReadyToAdoptByShelterId(shelter.getId(), 0, count);

        assertThat(actual)
                .isNotNull()
                .hasSize(adopters.size())
                .containsExactlyInAnyOrderElementsOf(adopters);
    }

    @Test
    void getAllReadyToAdoptByShelterIdWhenNone() {

        List<Adopter> adopters = Collections.emptyList();
        Page<Adopter> page = new PageImpl<>(adopters);

        when(adopterRepository.getAllReadyToAdoptByShelterId(any(Long.class),
                any(Pageable.class)))
                .thenReturn(page);

        List<Adopter> actual = adopterService.getAllReadyToAdoptByShelterId(0L, 0, 1);

        assertThat(actual)
                .isNotNull()
                .hasSize(0)
                .containsExactlyInAnyOrderElementsOf(adopters);
    }


    @Test
    void setAdopterForPet() {
        Pet pet = DataGenerator.generatePet();
        Adopter adopter = DataGenerator.generateAdopter(pet.getShelter());

        when(petRepository.getById(pet.getId())).thenReturn(pet);
        when(petRepository.save(pet)).thenReturn(pet);

        Pet actual = adopterService.setAdopterForPet(pet.getId(), adopter);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(pet);

        assertThat(actual.getAdopter())
                .isNotNull()
                .isEqualTo(adopter);

        assertThat(actual.getAdoptionDate())
                .isNotNull()
                .isAfter(LocalDate.now());

    }

    @Test
    void prolongTrialForNDays() {
        Pet pet = DataGenerator.generatePet();
        Long days = (long) DataGenerator.generateCount();

        when(petRepository.getById(pet.getId())).thenReturn(pet);
        when(petRepository.save(pet)).thenReturn(pet);

        Pet actual = adopterService.prolongTrialForNDays(pet.getId(), days);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(pet);

        assertThat(actual.getAdoptionDate())
                .isNotNull()
                .isAfter(LocalDate.now());
    }

    @Test
    void cancelTrial() {
        Pet pet = DataGenerator.generatePet();

        when(petRepository.getById(pet.getId())).thenReturn(pet);
        when(petRepository.save(pet)).thenReturn(pet);

        Pet actual = adopterService.cancelTrial(pet.getId());

        assertThat(actual)
                .isNotNull()
                .isEqualTo(pet);

        assertThat(actual.getAdoptionDate())
                .isNull();

        assertThat(actual.getAdopter())
                .isNull();

    }



}