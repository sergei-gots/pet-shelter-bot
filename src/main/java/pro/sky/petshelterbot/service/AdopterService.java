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

    public Pet setAdopter(Long petId, Adopter adopter) {
        Pet pet = petRepository.getPetById(petId);
        pet.setAdopter(adopter);
        pet.setAdoptionDate(LocalDate.now());
        petRepository.save(pet);
        return pet;
    }

    public Pet prolongTrialForNDays(Long petId, Integer days) {
        Pet pet = petRepository.getPetById(petId);
        LocalDate adoptionDate = pet.getAdoptionDate();
        if (adoptionDate == null) {
            pet.setAdoptionDate(LocalDate.now());
        } else {
            pet.setAdoptionDate(adoptionDate.plus(days, ChronoUnit.DAYS));
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

    public Adopter getAdopter(Long adopterId) {
        return adopterRepository.findByChatId(adopterId).get();
    }
}
