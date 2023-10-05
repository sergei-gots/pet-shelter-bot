package pro.sky.petshelterbot.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.exceptions.ShelterException;

import java.util.Optional;

public interface AdopterRepository extends JpaRepository<Adopter, Long> {

    Optional<Adopter> findByChatId(Long chatId);

    @NotNull
    default Adopter getByChatId(@NotNull Long chatId) {
        return findByChatId(chatId)
                .orElseThrow(
                        () -> ShelterException.of(Adopter.class, chatId )
                );
    }

    @Query("from Adopter a where a.shelter.id = :shelterId and not exists (select p.adopter from Pet p where p.adopter = a)")
    Page<Adopter> getAllReadyToAdoptByShelterId(Long shelterId, Pageable pageable);
}
