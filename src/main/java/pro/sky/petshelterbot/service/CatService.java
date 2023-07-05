package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.repository.PetRepository;

@Service
public class CatService {
    final private PetRepository catRepository;

    public CatService(PetRepository catRepository) {
        this.catRepository = catRepository;
    }

    public Pet addTestCatToDb() {
        Pet cat = new Pet(Pet.Species.CAT, "Муська");
        return catRepository.save(cat);
    }


}
