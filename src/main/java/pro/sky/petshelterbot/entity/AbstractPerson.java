package pro.sky.petshelterbot.entity;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractPerson implements Person {
    /**
     * Person will be identified by its chat_id which is persistent within
     * Telegram
     */
    @Id
    private Long chatId;
    private String firstName;

    private String phoneNumber;

    /** Currently chosen shelter within telegram bot chat **/
    @JoinColumn(name = "shelter_id")
    @ManyToOne
    private Shelter shelter;

    /** indicates state of chat for the user **/
    private ChatState chatState = ChatState.MENU_NAVIGATION;

    /** Telegram message id for the message containing last depicted menu in the chat **/
    private Integer chatMenuMessageId;

    public AbstractPerson() {
    }

    public AbstractPerson(Long chatId, String firstName) {
        this.chatId = chatId;
        this.firstName = firstName;
    }

    public AbstractPerson(Long chatId, String firstName, Shelter shelter) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.shelter = shelter;
    }
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public ChatState getChatState() {
        return chatState;
    }

    public void setChatState(ChatState chatState) {
        this.chatState = chatState;
    }

    @Override
    public String toString() {
        return  "chatId=" + chatId +
                ", firstName='" + firstName + "'," +
                ", chatShelter =" + shelter + "'," +
                ", chatState =" + chatState + "'," +
                ", chatMenuMessageId=" + chatMenuMessageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPerson that = (AbstractPerson) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(firstName, that.firstName) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(shelter, that.shelter) && chatState == that.chatState && Objects.equals(chatMenuMessageId, that.chatMenuMessageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, firstName, phoneNumber, shelter, chatState, chatMenuMessageId);
    }
}
