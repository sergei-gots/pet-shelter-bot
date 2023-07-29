package pro.sky.petshelterbot.service;

import org.jetbrains.annotations.NotNull;
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
import java.util.Collections;
import java.util.List;

@Service
public class AdopterService {

    public final int BASIC_TRIAL_DAYS = 14;
    private final AdopterRepository adopterRepository;
    private final PetRepository petRepository;

    public AdopterService(AdopterRepository adopterRepository, PetRepository petRepository) {
        this.adopterRepository = adopterRepository;
        this.petRepository = petRepository;
    }

    public List<Adopter> getAllReadyToAdoptByShelterId(Long shelterId, Integer pageNb, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNb, pageSize);
        Page<Adopter> adopters = adopterRepository.getAllReadyToAdoptByShelterId(shelterId, pageable);
        return  (adopters.hasContent()) ?
                adopters.getContent() :
                Collections.emptyList();
    }

    public Pet setAdopterForPet(@NotNull Long petId, Adopter adopter) {
        Pet pet = petRepository.getById(petId);
        pet.setAdopter(adopter);
        pet.setAdoptionDate(LocalDate.now().plus(BASIC_TRIAL_DAYS, ChronoUnit.DAYS));
        return petRepository.save(pet);
    }

    public Pet prolongTrialForNDays(@NotNull Long petId, Long days) {
        Pet pet = petRepository.getById(petId);
        LocalDate adoptionDate = pet.getAdoptionDate();
        if (adoptionDate == null) {
            adoptionDate = LocalDate.now();
        }
        adoptionDate = adoptionDate.plus(days, ChronoUnit.DAYS);
        pet.setAdoptionDate(adoptionDate);
        return petRepository.save(pet);
    }

    public Pet cancelTrial(@NotNull Long petId) {
        Pet pet = petRepository.getById(petId);
        pet.setAdopter(null);
        pet.setAdoptionDate(null);
        petRepository.save(pet);
        return pet;
    }

    public Adopter get(@NotNull Long chatId) {
        return adopterRepository.getByChatId(chatId);
    }
}
