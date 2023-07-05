package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.ShelterRepository;

@Service
public class ShelterService {
    final private ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

}
