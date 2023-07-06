package pro.sky.petshelterbot.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractPerson {
    /**
     * Person will be identified by its chat_id which is persistent within
     * Telegram
     */
    @Id
    private long chatId;
    private String firstName;

    public AbstractPerson() {
    }

    public AbstractPerson(long chatId, String firstName) {
        this.chatId = chatId;
        this.firstName = firstName;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return  "chatId=" + chatId +
                ", firstName='" + firstName + '\'';
    }
}
