package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Shelter;

import java.util.NoSuchElementException;
import java.util.Optional;

public interface ShelterRepository extends JpaRepository<Shelter,Long> {
    Optional<Shelter> findByType(String type);

    /**
     *
     *
     * @throws NoSuchElementException if there isn't shelter corresponding to the passed value of Shelter.SHELTER_TYPE
     */
    default Shelter getByType(Shelter.SHELTER_TYPE shelterType) {
        String type = shelterType.name().toLowerCase();
        return findByType(type)
                .orElseThrow(() ->
                        new NoSuchElementException("No shelter with type = \"" + type + "\""));
    }
    default Shelter getCatShelter() {
        return getByType(Shelter.SHELTER_TYPE.CAT);
    }

    default Shelter getDogShelter() {
        return getByType(Shelter.SHELTER_TYPE.DOG);
    }

    Shelter getSheltersById(Long id);
}
