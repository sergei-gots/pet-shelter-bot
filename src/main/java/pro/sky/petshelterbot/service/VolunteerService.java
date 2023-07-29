package pro.sky.petshelterbot.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.VolunteerRepository;

import java.util.List;

@Service
public class VolunteerService {
    final private VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    public Volunteer add(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    public Volunteer delete(Volunteer volunteer) {
        volunteerRepository.delete(volunteer);
        return volunteer;
    }
    public List<Volunteer> getAllVolunteersByShelterId(Long shelterId) {
        return volunteerRepository.findAllByShelterId(shelterId);
    }

    public Volunteer get(@NotNull Long chatId) {
        return volunteerRepository.getByChatId(chatId);
    }
}


