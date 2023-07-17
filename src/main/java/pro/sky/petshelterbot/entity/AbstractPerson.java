package pro.sky.petshelterbot.entity;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    /** Currently chosen shelter within telegram bot chat **/
    @JoinColumn(name = "shelter_id")
    @ManyToOne
    private Shelter shelter;

    /** Telegram message id for the message containing last depicted menu in the chat **/
    private Integer chatMenuMessageId;

    public AbstractPerson() {
    }

    public AbstractPerson(long chatId, String firstName) {
        this.chatId = chatId;
        this.firstName = firstName;
    }

    public AbstractPerson(long chatId, String firstName, Shelter shelter) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.shelter = shelter;
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

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    public Integer getChatMenuMessageId() {
        return chatMenuMessageId;
    }

    public void resetChatMenuMessageId() {
        this.chatMenuMessageId = null;
    }
    public void setChatMenuMessageId(Integer chatMenuMessageId) {
        this.chatMenuMessageId = chatMenuMessageId;
    }

    @Override
    public String toString() {
        return  "chatId=" + chatId +
                ", firstName='" + firstName + "'," +
                ", chatShelter =" + shelter + "'," +
                ", chatMenuMessageId=" + chatMenuMessageId;
    }
}
