package pro.sky.petshelterbot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.repository.AdopterRepository;
import pro.sky.petshelterbot.repository.PetRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdopterService {

    private final AdopterRepository adopterRepository;
    private final PetRepository petRepository;

    public AdopterService(AdopterRepository adopterRepository, PetRepository petRepository) {
        this.adopterRepository = adopterRepository;
        this.petRepository = petRepository;
    }

    public List<Adopter> getAllReadyToAdopt(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Adopter> adopters = adopterRepository.getAllReadyToAdopt(pageable);
        if (adopters.hasContent()) {
            return adopters.getContent();
        } else {
            return new ArrayList<Adopter>();
        }
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
        pet.setAdopter(null);
        petRepository.save(pet);
        return pet;
    }
}
