package pro.sky.petshelterbot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.exceptions.ShelterException;
import pro.sky.petshelterbot.repository.PetRepository;
import pro.sky.petshelterbot.util.FileManager;

import java.util.Collections;
import java.util.List;

@Service
public class PetService {
    final private PetRepository petRepository;
    final private FileManager fileManager;

    public PetService(
            PetRepository catRepository,
            FileManager fileManager) {

        this.petRepository = catRepository;
        this.fileManager = fileManager;
    }

    public Pet add(Pet pet, MultipartFile img) {
        if (!pet.getShelter().getType().equals(pet.getSpecies())) {
            throw new ShelterException("Тип животного не соответствует типу размещаемых животных в приюте.");
        }
        return addImg(pet.getId(), img);
    }

    public Pet add(Pet pet) {
        if (pet.getShelter().getType().equals(pet.getSpecies())) {
            return petRepository.save(pet);
        }
        throw new ShelterException("Тип животного не соответствует типу размещаемых животных в приюте.");
    }

    public Pet addImg(Long petId, MultipartFile img) {
        Pet pet = get(petId);
        String imgPath = fileManager.uploadPetImg(pet, img);
        if (imgPath != null) {
            pet.setImgPath(imgPath);
            pet = petRepository.save(pet);
        }
        return pet;
    }

    public Pet add(String species, String name, Shelter shelter) {
        Pet pet = new Pet(species, name, shelter);
        return petRepository.save(pet);
    }

    public Pet get(Long id) {
        return petRepository.getById(id);
    }

    public Pet delete(Pet pet) {
        //Check if it is in the db
        get(pet.getId());
        petRepository.delete(pet);
        return pet;
    }

    /* GET /dog-shelter/pets */
    public List<Pet> getByShelterId(Long shelterId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Pet> petsPage = petRepository.findAllByShelterId(shelterId, pageable);
        return (petsPage.hasContent()) ?
                petsPage.getContent() : Collections.emptyList();
    }
}
