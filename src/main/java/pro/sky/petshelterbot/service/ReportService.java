package pro.sky.petshelterbot.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;
import pro.sky.petshelterbot.repository.ReportRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report get(Long id) {
        return reportRepository.getById(id);
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

    public List<Pet> getOverdueReports(Long shelterId) {

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

    public Report approve(@NotNull Long id) {
        Report report = get(id);
        report.setApproved(true);
        report.setChecked(true);
        return update(report);
    }

    public Report disapprove(@NotNull Long id) {
        Report report = get(id);
        report.setApproved(false);
        report.setChecked(true);
        return update(report);
    }
}
