package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.UserMessage;

import java.util.Optional;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {


    Optional<UserMessage> findFirstByNameAndShelterId(String name, Long shelterId);
    Optional<UserMessage> findFirstByNameAndShelterIsNull(String name);

}
