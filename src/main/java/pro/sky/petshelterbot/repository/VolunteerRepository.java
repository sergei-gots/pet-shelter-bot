package pro.sky.petshelterbot.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Volunteer;
import pro.sky.petshelterbot.exceptions.ShelterException;

import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    Optional<Volunteer> findByChatId(Long chatId);

    @NotNull
    default Volunteer getByChatId(@NotNull Long chatId) {
        return findByChatId(chatId)
                .orElseThrow(() -> ShelterException.of(
                        Volunteer.class, chatId)
        );
    }

    List<Volunteer> findAllByShelterId(Long shelterId);
  
    List<Volunteer> findByShelterAndAvailableIsTrue(Shelter shelter);

}