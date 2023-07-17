package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Dialog;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Volunteer;

import java.util.Collection;
import java.util.Optional;

public interface DialogRepository extends JpaRepository<Dialog, Long> {

    Optional<Dialog> findByVolunteer(Volunteer volunteer);
    Optional<Dialog> findByAdopter(Adopter adopter);

    Optional<Dialog> findByVolunteerChatId(Long volunteerChatId);


    Optional<Dialog> findByAdopterChatId(Long adopterChatId);

    Optional<Dialog> findByAdopterChatIdAndVolunteerIsNotNull(Long adopterChatId);


    /**
     * @return Dialog entry with the least ID  where Volunteer==null within specified Shelter
     */
    @Query("from Dialog d where d.volunteer = null and d.adopter.chatShelter = :shelter order by d.id asc")
    Collection<Dialog> findWaitingDialogsByVolunteerShelterOrderByIdAsc(Shelter shelter);



}
