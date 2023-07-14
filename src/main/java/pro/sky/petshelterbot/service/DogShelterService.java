package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.exceptions.ShelterException;
import pro.sky.petshelterbot.repository.PetRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;

import java.util.List;

@Service
public class DogShelterService {
    final private PetRepository petRepository;
    final private ShelterRepository shelterRepository;

    final private Shelter catShelter, dogShelter;

    public DogShelterService(PetRepository catRepository, ShelterRepository shelterRepository) {

        this.petRepository = catRepository;
        this.shelterRepository = shelterRepository;
        catShelter = shelterRepository.getCatShelter();
        dogShelter = shelterRepository.getDogShelter();
    }

    public Pet createDog(Pet pet) {
        if (!pet.getSpecies().equals("dog")) {
            throw new ShelterException("Проверьте тип животного.");
        }
        if (!pet.getShelter().getType().equals("dog")) {
            throw new ShelterException("Проверьте тип приюта.");
        }
        return petRepository.save(pet);
    }

    /*  */
    public List<Pet> findAll() {
        return null;
    }

    public Pet add(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet add(String species, String name, Shelter shelter) {
        Pet pet = new Pet(species, name, shelter);
        return petRepository.save(pet);
    }

    public Pet get(Long id) {
        Pet pet = petRepository.getPetById(id);
        if (pet == null) {
            throw new ShelterException(
                    String.format("Животного с Id# %d не существует.", id)
            );
        }
        return pet;
    }

    public Pet delete(Long id) {
        Pet pet = get(id);
        petRepository.delete(pet);
        return pet;
    }

}
