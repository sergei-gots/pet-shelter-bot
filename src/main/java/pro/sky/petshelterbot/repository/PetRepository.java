package pro.sky.petshelterbot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Pet;

import java.util.Collection;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet,Long> {

    Pet getPetById(Long id);

    Page<Pet> findAllByShelterId(Long shelterId, Pageable pageable);

}
