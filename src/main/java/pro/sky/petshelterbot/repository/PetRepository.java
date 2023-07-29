package pro.sky.petshelterbot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.exceptions.ShelterException;

import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet,Long> {

    Page<Pet> findAllByShelterId(Long shelterId, Pageable pageable);

    @NotNull
    @Override
    default Pet getById(@NotNull Long id) {
        return findById(id)
                .orElseThrow(
                        () -> new ShelterException("The pet with id=" + id + " is not listed in the db")
                );
    }

    @Query("from Pet p where p.adopter = :adopter and p.shelter = :shelter and p.adoptionDate is not null and p.adoptionDate >= current_date")
    Optional<Pet> findFirstByAdopterAndShelter(Adopter adopter, Shelter shelter);
}
