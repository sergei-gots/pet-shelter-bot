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
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository, PetRepository petRepository) {
        this.reportRepository = reportRepository;
    }

    public Report get(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(()->new ShelterException("Report with id=" + id + " is not listed in the db."));
    }
    public List<Report> getAllByPetId(Long petId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Report> reportPage = reportRepository.findAllByPetId(petId, pageable);
        if (reportPage.hasContent()) {
            return reportPage.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pet> findAllOverdueReports() {
        return reportRepository.findAllOverdueReports();
    }

    public List<Pet> findOverdueReports(Long shelterId) {

        return reportRepository.findOverdueReports(shelterId);
    }

    public List<Report> getAllByShelterId(Long shelterId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Report> reportPage = reportRepository.findAllByShelterId(shelterId, pageable);
        if (reportPage.hasContent()) {
            return reportPage.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    public Report update(Report report) {
        return reportRepository.save(report);
    }

    public List<Report> getAllReportsByShelterIdToReview(Long shelterId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Report> reportPage = reportRepository.findAllUncheckedReports(shelterId, pageable);
        if (reportPage.hasContent()) {
            return reportPage.getContent();
        } else {
            return new ArrayList<>();
        }
    }
}
