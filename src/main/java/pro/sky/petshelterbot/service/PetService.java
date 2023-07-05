package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.repository.PetRepository;

@Service
public class PetService {
    final private PetRepository petRepository;

    public PetService(PetRepository catRepository) {
        this.petRepository = catRepository;
    }

}
