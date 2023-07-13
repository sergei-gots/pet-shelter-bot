package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.repository.PetRepository;
import pro.sky.petshelterbot.repository.ReportRepository;

import java.util.Collection;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final PetRepository petRepository;

    public ReportService(ReportRepository reportRepository, PetRepository petRepository) {
        this.reportRepository = reportRepository;
        this.petRepository = petRepository;
    }

    public List<Report> findAllByCatId(Long petId) {
        Pet pet = petRepository.getPetById(petId);
        if (pet.getSpecies().equals("cat")) {
            return reportRepository.findAllByPet(pet);
        }
        return null;
    }
}
