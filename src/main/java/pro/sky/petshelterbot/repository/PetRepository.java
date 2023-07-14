package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Pet;

import java.util.Collection;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet,Long> {

    Pet getPetById(Long id);

    //@Query("select p, extract(day from current_timestamp - p.adoptionDate) as countDay, (select count(r.id) from Report r) as countReports from Pet p where p.adoptionDate != null")
    @Query("from Pet p where p.adoptionDate != null and (select count(r.pet) from Report r) != (extract(day from current_timestamp - p.adoptionDate))")
    List<Pet> getPetByAdoptionDateIsNotNull();

}
