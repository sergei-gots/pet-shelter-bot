package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.VolunteerRepository;
import pro.sky.petshelterbot.util.DataGenerator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VolunteerServiceTest {

    @MockBean
    private VolunteerRepository volunteerRepository =  Mockito.mock(VolunteerRepository.class);

    private final VolunteerService volunteerService = new VolunteerService(volunteerRepository);

    @Test
    void findAllByShelterId() {
        int nVolunteersCount = DataGenerator.generateCount();
        Shelter shelter = DataGenerator.generateShelter();
        Long shelterId = shelter.getId();
        List<Volunteer> volunteers = Stream.generate(DataGenerator::generateVolunteer)
                .limit(nVolunteersCount)
                .collect(Collectors.toList());

        when(volunteerRepository.findAllByShelterId(shelterId))
                .thenReturn(volunteers);

        Collection<Volunteer> actual = volunteerService
                .getAllVolunteersByShelterId(shelterId);

        assertThat(actual)
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(volunteers);
        assertThat(actual.size()).isEqualTo(nVolunteersCount);
    }

    @Test
    void add_get() {
        Volunteer volunteer = DataGenerator.generateVolunteer();

        when(volunteerRepository.save(any())).thenReturn(volunteer);
        when(volunteerRepository.findByChatId(volunteer.getChatId()))
                .thenReturn(Optional.of(volunteer));

        Volunteer actual = volunteerService.add(volunteer);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(volunteer);

        Volunteer actualGet = volunteerService.get(actual.getChatId());
        assertThat(actualGet)
                .isNotNull()
                .isEqualTo(volunteer);
    }

    @Test
    void delete() {
        Volunteer volunteer = DataGenerator.generateVolunteer();

        Volunteer actual = volunteerService.delete(volunteer);

        assertThat(actual)
                .isNotNull()
                .isEqualTo(volunteer);
    }

}