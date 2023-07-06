package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Pet;

public interface PetRepository extends JpaRepository<Pet,Long> {
}
