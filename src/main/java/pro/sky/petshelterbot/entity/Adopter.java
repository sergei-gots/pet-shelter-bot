package pro.sky.petshelterbot.entity;


import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "adopters")
public class Adopter extends AbstractPerson {

    /** Telegram message id for the message containing last depicted menu in the chat **/
    private Integer chatMenuMessageId;

    /** Currently chosen shelter within telegram bot chat **/
    @JoinColumn(name = "chat_shelter_id")
    @ManyToOne
    private Shelter chatShelter;

    public Adopter() {
    }

    public Adopter(long chatId) {
        super(chatId, null);
    }

    public Adopter(long chatId, String firstName) {
        super(chatId, firstName);
    }

    public Adopter(long chatId, String firstName, Integer chatMenuMessageId)
    {
        super(chatId, firstName);
        this.chatMenuMessageId = chatMenuMessageId;
    }

    @Override
    public String toString() {
        return "Adopter{" +
                super.toString() +
                ", chatMenuMessageId=" + chatMenuMessageId +
                ", chatShelter =" + chatShelter +
                "}";
    }

    public int getChatMenuMessageId() {
        return chatMenuMessageId;
    }

    public void setChatMenuMessageId(Integer chatMenuMessageId) {
        this.chatMenuMessageId = chatMenuMessageId;
    }

    public Shelter getChatShelter() {
        return chatShelter;
    }

    public void setChatShelter(Shelter chatShelter) {
        this.chatShelter = chatShelter;
    }
}


