package pro.sky.petshelterbot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.repository.PetRepository;
import pro.sky.petshelterbot.repository.ReportRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final PetRepository petRepository;

    public ReportService(ReportRepository reportRepository, PetRepository petRepository) {
        this.reportRepository = reportRepository;
        this.petRepository = petRepository;
    }

    public List<Report> findAllByCatId(Long petId, Integer pageNo, Integer pageSize) {
        if (getPetSpecies(petId).equals("cat")) {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Report> reportPage = reportRepository.findAllByPetId(petId, pageable);
            if(reportPage.hasContent()) {
                return reportPage.getContent();
            } else {
                return new ArrayList<Report>();
            }
        }
        return null;
    }

    public List<Report> findAllByDogId(Long petId, Integer pageNo, Integer pageSize) {
        if (getPetSpecies(petId).equals("dog")) {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Report> reportPage = reportRepository.findAllByPetId(petId, pageable);
            if(reportPage.hasContent()) {
                return reportPage.getContent();
            } else {
                return new ArrayList<Report>();
            }
        }
        return null;
    }

    public Report get(Long id) {
        return reportRepository.findById(id).get();
    }

    public Report updateReportCat(Long id, Boolean checked, Boolean approved) {
        Report report = get(id);
        if (!report.getPet().getSpecies().equals("cat")) {
            return null;
        }
        if (checked != null) {
            report.setChecked(checked);
        }
        if (approved != null) {
            report.setApproved(approved);
        }
        return reportRepository.save(report);
    }

    public Report updateReportDog(Long id, Boolean checked, Boolean approved) {
        Report report = get(id);
        if (!report.getPet().getSpecies().equals("dog")) {
            return null;
        }
        if (checked != null) {
            report.setChecked(checked);
        }
        if (approved != null) {
            report.setApproved(approved);
        }
        return reportRepository.save(report);
    }

    private String getPetSpecies(Long petId) {
        Pet pet = petRepository.getPetById(petId);
        return  pet.getSpecies();
    }
}
