package pro.sky.petshelterbot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Adopter;

import java.util.Optional;

public interface AdopterRepository extends JpaRepository<Adopter, Long> {
    Optional<Adopter> findByChatId(Long chatId);
    @Query("from Adopter a where not exists (select p.adopter from Pet p where p.adopter = a)")
    Page<Adopter> getAllReadyToAdopt(Pageable pageable);
}
