package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Button;

import java.util.Collection;

public interface ButtonsRepository extends JpaRepository<Button, Long> {

    @Query(value = "from Button b where b.shelterId = :shelter_id and b.chapter = :chapter order by b.id")
    Collection<Button> getButtonsByShelterId(Long shelter_id, String chapter);

}
