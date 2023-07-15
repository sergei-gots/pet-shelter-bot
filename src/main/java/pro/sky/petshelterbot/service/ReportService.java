package pro.sky.petshelterbot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private String getPetSpecies(Long petId) {
        Pet pet = petRepository.getPetById(petId);
        return pet.getSpecies();
    }

    public List<Report> findAllByPetId(Long petId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Report> reportPage = reportRepository.findAllByPetId(petId, pageable);
        if (reportPage.hasContent()) {
            return reportPage.getContent();
        } else {
            return new ArrayList<Report>();
        }
    }

    /* Нужно добавить shelter_id для выборки - в процессе */
    public List<Pet> findOverdueReports(Long shelterId) {
        return reportRepository.findOverdueReports(shelterId);
    }

    /**
     * GET /dog-shelter/reports/all
     *
     * @param shelterId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<Report> findAllReportsByShelterId(Long shelterId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Report> reportPage = reportRepository.findAllByShelterId(shelterId, pageable);
        if (reportPage.hasContent()) {
            return reportPage.getContent();
        } else {
            return new ArrayList<Report>();
        }
    }

    /**
     * PUT /dog-shelter/reports/
     *
     * @param report
     * @return
     */
    public Report updateReport(Report report) {
        if (!report.isApproved()) {
            Long adopterId = report.getPet().getAdopter().getChatId();
            /* Код отправки сообщения пользователю о ненадлежащем заполнении отчета. */
        }
        reportRepository.save(report);
        return report;
    }

    /* GET /dog-shelter/reports/to-review */
    public List<Report> findAllReportsToReview(Long shelterId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Report> reportPage = reportRepository.findAllUncheckedReports(shelterId, pageable);
        if (reportPage.hasContent()) {
            return reportPage.getContent();
        } else {
            return new ArrayList<Report>();
        }
    }
}
