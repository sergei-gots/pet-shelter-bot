package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Button;

import java.util.Collection;

public interface ButtonRepository extends JpaRepository<Button, Long> {

    Collection<Button> findByShelterIdAndChapterOrderById(Long shelter_id, String chapter);
    Collection<Button> findByChapterOrderById(String chapter);

}
