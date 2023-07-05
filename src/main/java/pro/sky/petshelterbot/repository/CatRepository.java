package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Pet;

public interface CatRepository extends JpaRepository<Pet,Long> {
}
