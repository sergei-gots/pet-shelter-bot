package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.entity.Shelter;

import java.util.Collection;
import java.util.Optional;

public interface ButtonRepository extends JpaRepository<Button, Long> {

    Collection<Button> findByShelterAndChapterOrderById(Shelter shelter, String chapter);
    Collection<Button> findByChapterAndShelterIsNullOrderById(String chapter);

}
