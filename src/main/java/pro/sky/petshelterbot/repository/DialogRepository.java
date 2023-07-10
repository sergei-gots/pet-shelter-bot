package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Volunteer;

import java.util.Optional;

public interface DialogRepository extends JpaRepository<Dialog, Long> {

    Optional<Dialog> findByVolunteer(Volunteer volunteer);
    Optional<Dialog> findByAdopter(Adopter adopter);

    Optional<Dialog> findByVolunteerChatId(Long volunteerChatId);

}
