package pro.sky.petshelterbot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.exceptions.ShelterException;
import pro.sky.petshelterbot.repository.PetRepository;
import pro.sky.petshelterbot.repository.ReportRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class DogShelterReportService {

    private final ReportRepository reportRepository;
    private final PetRepository petRepository;

    public DogShelterReportService(ReportRepository reportRepository, PetRepository petRepository) {
        this.reportRepository = reportRepository;
        this.petRepository = petRepository;
    }

    private String getPetSpecies(Long petId) {
        Pet pet = petRepository.getPetById(petId);
        return pet.getSpecies();
    }

    public List<Report> findAllByPetId(Long petId, Integer pageNo, Integer pageSize) {
        if (getPetSpecies(petId).equals("dog")) {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Report> reportPage = reportRepository.findAllByPetId(petId, pageable);
            if (reportPage.hasContent()) {
                return reportPage.getContent();
            } else {
                return new ArrayList<Report>();
            }
        }
        throw new ShelterException("Ошибка получения отчёта.");
    }

    public List<Pet> findOverdueReports() {
        List<Pet> pets = new ArrayList<>();
        petRepository.getPetByAdoptionDateIsNotNull()
                .forEach(pet -> {
                    if (pet.getShelter().getType().equals("dog")) {
                        pets.add(pet);
                    }
                });
        if (pets.size() > 0) {
            return pets;
        }
        throw new ShelterException("Нарушителей не выявлено.");
    }
}
