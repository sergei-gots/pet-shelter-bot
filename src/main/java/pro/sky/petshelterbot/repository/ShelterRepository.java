package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Shelter;

public interface ShelterRepository extends JpaRepository<Shelter,Long> {
    Shelter getShelterById(Long id);
}
