package pro.sky.petshelterbot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.exceptions.ShelterException;
import pro.sky.petshelterbot.repository.PetRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.VolunteerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class DogShelterService {
    final private PetRepository petRepository;
    final private ShelterRepository shelterRepository;
    final private VolunteerRepository volunteerRepository;

    final private Shelter catShelter, dogShelter;

    public DogShelterService(
            PetRepository catRepository,
            ShelterRepository shelterRepository,
            VolunteerRepository volunteerRepository) {

        this.petRepository = catRepository;
        this.shelterRepository = shelterRepository;
        catShelter = shelterRepository.getCatShelter();
        dogShelter = shelterRepository.getDogShelter();
        this.volunteerRepository = volunteerRepository;
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

    /* GET /dog-shelter/pets */
    public List<Pet> findAllPets(Long shelterId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Pet> petsPage = petRepository.findAllByShelterId(shelterId, pageable);
        if (petsPage.hasContent()) {
            return petsPage.getContent();
        } else {
            return new ArrayList<Pet>();
        }
    }

    /* POST /dog-shelter/volunteers/ */
    public Volunteer AddVolunteerToShelter(Volunteer volunteer) {
        if (volunteer.isAvailable()) {
            return volunteerRepository.save(volunteer);
        }
        throw new ShelterException("Пользователь не найден или недоступен для использования в качестве волонтёра.");
    }

    /* GET /dog-shelter/volunteers/ */
    public List<Volunteer> findAllVolunteersByShelterId(Long shelterId) {
        return volunteerRepository.findAllByShelterId(shelterId);
    }

    public Volunteer deleteVolunteer(Volunteer volunteer) {
        volunteerRepository.delete(volunteer);
        return volunteer;
    }
}
