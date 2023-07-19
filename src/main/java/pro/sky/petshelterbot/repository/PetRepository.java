package pro.sky.petshelterbot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet,Long> {

    Pet getPetById(Long id);

    Page<Pet> findAllByShelterId(Long shelterId, Pageable pageable);

    @Query("from Pet p where p.adopter = :adopter and p.shelter = :shelter and p.adoptionDate is not null and p.adoptionDate >= current_date")
    Optional<Pet> findFirstByAdopterAndShelter(Adopter adopter, Shelter shelter);
}
