package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;

import java.util.Collection;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    /** all the reports by Pet-Id **/
    List<Report> findByPetId(Long PetId);
    /** all unchecked reports **/
    List<Report> findByCheckedIsFalse();
    /** all checked reports for which the advice to improve should be sent
     * TODO think about how to manage or change this note**/
    List<Report> findByCheckedIsTrueAndApprovedIsFalse();

    Collection<Report> findAllByPet(Pet pet);
}
