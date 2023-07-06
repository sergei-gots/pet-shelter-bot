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

    public Pet createCat(String name) {
        return petRepository.save(new Pet (Pet.Species.CAT, name));
    }

    public Pet createDog(String name) {
        return petRepository.save(new Pet (Pet.Species.DOG, name));
    }

}
