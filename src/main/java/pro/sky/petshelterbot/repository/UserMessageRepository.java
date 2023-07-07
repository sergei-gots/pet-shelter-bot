package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.UserMessage;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {

    @Query(value = "select um.message from UserMessage um where um.key = :key")
    public String getMessageByKey(String key);

}
