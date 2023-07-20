package pro.sky.petshelterbot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Report;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    /** all the reports by Pet-Id **/
    List<Report> findByPetId(Long PetId);
    /** all unchecked reports **/
    List<Report> findByCheckedIsFalse();
    /** all checked reports for which the advice to improve should be sent
     * TODO think about how to manage or change this note**/
    List<Report> findByCheckedIsTrueAndApprovedIsFalse();

    /**
     * @return the latest checked report for the pet
     */
    Optional<Report> findLastByPetAndCheckedIsTrueAndSentIsNotNullOrderBySentAsc(Pet pet);

    Page<Report> findAllByPetId(Long petId, Pageable pageable);

    @Query("from Report r where r.pet.shelter.id = :shelterId")
    Page<Report> findAllByShelterId(Long shelterId, Pageable pageable);

    @Query("from Report r where r.checked = null or r.checked = false and r.pet.shelter.id = :shelterId")
    Page<Report> findAllUncheckedReports(Long shelterId, Pageable pageable);

    @Query("select p from Pet p left join Report r on p = r.pet where p.shelter.id = :shelterId and not exists(select r from Report r where r.sent >= current_date - 1) and p.adoptionDate is not null and r.sent is not null group by p")
    List<Pet> findOverdueReports(Long shelterId);

    Optional<Report> findFirstByPetAndSentIsNull(Pet pet);
}
