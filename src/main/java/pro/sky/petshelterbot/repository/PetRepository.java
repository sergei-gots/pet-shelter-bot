package pro.sky.petshelterbot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Pet getPetById(Long id);

    Page<Pet> findAllByShelterId(Long shelterId, Pageable pageable);

}
