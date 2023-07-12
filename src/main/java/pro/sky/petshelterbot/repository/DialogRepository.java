package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Volunteer;

import java.util.Optional;

public interface DialogRepository extends JpaRepository<Dialog, Long> {

    Optional<Dialog> findByVolunteer(Volunteer volunteer);
    Optional<Dialog> findByAdopter(Adopter adopter);

    Optional<Dialog> findByVolunteerChatId(Long volunteerChatId);

    /**
     *  for production purpose: when we can have only one waiting
     *  dialog (i.e. adopter) for any adopter.
     */

    Optional<Dialog> findByAdopterChatId(Long adopterChatId);

    /**
     *  for developing purpose: when we can have many waiting
     *  dialogs (i.e. adopters) for the same adopter.
     *
     */
    Optional<Dialog> findFirstByAdopterChatId(Long adopterChatId);

    Optional<Dialog> findFirstByVolunteerIsNullAndShelterOrderByIdAsc(Shelter shelter);

}
