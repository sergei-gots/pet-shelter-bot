package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Cat;

public interface CatRepository extends JpaRepository<Cat,Long> {
}
