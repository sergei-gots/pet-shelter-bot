package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Shelter;

import java.util.Optional;

public interface ShelterRepository extends JpaRepository<Shelter,Long> {
    Optional<Shelter> findByType(String type);
    default Shelter getCatShelter() { return findByType("cat").orElseThrow(); }
    default Shelter getDogShelter() { return findByType("dog").orElseThrow(); }
}
