package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Report;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPetId(Long PetId);
    List<Report> findByCheckedIsFalse();
    List<Report> findByCheckedIsTrueAndApprovedIsFalse();

}
