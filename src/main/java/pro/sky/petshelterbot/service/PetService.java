package pro.sky.petshelterbot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.exceptions.ShelterException;
import pro.sky.petshelterbot.repository.PetRepository;
import pro.sky.petshelterbot.repository.ShelterRepository;
import pro.sky.petshelterbot.repository.VolunteerRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class PetService {
    final private PetRepository petRepository;
    final private ShelterRepository shelterRepository;
    final private VolunteerRepository volunteerRepository;

    /*final private Shelter catShelter, dogShelter;*/

    public PetService(
            PetRepository catRepository,
            ShelterRepository shelterRepository,
            VolunteerRepository volunteerRepository) {

        this.petRepository = catRepository;
        this.shelterRepository = shelterRepository;
        /*catShelter = shelterRepository.getCatShelter();
        dogShelter = shelterRepository.getDogShelter();*/
        this.volunteerRepository = volunteerRepository;
    }

    private static String UPLOAD_DIRECTORY = "src/main/resources/uploads/pet_images";

    private String imgUploader(Long id, MultipartFile img) {
        try {
            String fileName = String.format(
                    "%d.%s",
                    id,
                    StringUtils.getFilenameExtension(img.getOriginalFilename())
            );
            byte[] data = img.getBytes();
            Path path = Paths.get(UPLOAD_DIRECTORY, fileName);
            Files.write(path, data);
            return path.toString();
        } catch (Exception e) {
        }
        return null;
    }

    public Pet add(Pet pet, MultipartFile img) {
        if (pet.getShelter().getType().equals(pet.getSpecies())) {
            String imgPath = imgUploader(pet.getId(), img);
            if (imgPath != null) {
                pet.setImgPath(imgPath);
            }
            return petRepository.save(pet);
        }
        throw new ShelterException("Тип животного не соответствует типу размещаемых животных в приюте.");
    }

    public Pet add(Pet pet) {
        if (pet.getShelter().getType().equals(pet.getSpecies())) {
            return petRepository.save(pet);
        }
        throw new ShelterException("Тип животного не соответствует типу размещаемых животных в приюте.");
    }

    public Pet addImg(Long petId, MultipartFile img) {
        Pet pet = petRepository.getPetById(petId);
        if (pet == null) {
            throw new ShelterException("Питомец не найден.");
        }
        String imgPath = imgUploader(petId, img);
        if (imgPath != null) {
            pet.setImgPath(imgPath);
        }
        petRepository.save(pet);
        return pet;
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

    public Pet delete(Pet pet) {
        if (petRepository.findById(pet.getId()).isEmpty()) {
            throw new ShelterException("Животное не найдено.");
        }
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
}
