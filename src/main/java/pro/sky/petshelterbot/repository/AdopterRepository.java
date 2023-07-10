package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Adopter;

import java.util.Optional;

public interface AdopterRepository extends JpaRepository<Adopter, Long> {

    Optional<Adopter> findByChatId(Long chatId);

}
