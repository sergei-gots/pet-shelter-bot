package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.entity.UserMessage;


public interface UserMessageRepository extends JpaRepository<UserMessage,UserMessage.Key> {


}
