package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.repository.PetRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet setAdopter(Pet pet, Adopter adopter) {
        pet.setAdopter(adopter);
        pet.setAdoptionDate(LocalDate.now());
        petRepository.save(pet);
        return pet;
    }

    public Pet prolongTrial14(Long petId) {
        Pet pet = petRepository.getPetById(petId);
        LocalDate adoptionDate = pet.getAdoptionDate();
        if (adoptionDate == null) {
            pet.setAdoptionDate(LocalDate.now());
        } else {
            pet.setAdoptionDate(adoptionDate.plus(14, ChronoUnit.DAYS));
        }
        petRepository.save(pet);
        return pet;
    }

    public Pet prolongTrial30(Long petId) {
        Pet pet = petRepository.getPetById(petId);
        LocalDate adoptionDate = pet.getAdoptionDate();
        if (adoptionDate == null) {
            pet.setAdoptionDate(LocalDate.now().plus(30, ChronoUnit.DAYS));
        } else {
            pet.setAdoptionDate(adoptionDate.plus(30, ChronoUnit.DAYS));
        }
        petRepository.save(pet);
        return pet;
    }

    public Pet cancelTrial(Long petId) {
        Pet pet = petRepository.getPetById(petId);
        pet.setAdoptionDate(null);
        petRepository.save(pet);
        return pet;
    }
}
