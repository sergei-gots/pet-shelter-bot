package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.repository.VolunteerRepository;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    public Volunteer deleteVolunteer(Volunteer volunteer) {
        volunteerRepository.delete(volunteer);
        return volunteer;
    }

}
