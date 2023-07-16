package pro.sky.petshelterbot.entity;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "adopters")
public class Adopter extends AbstractPerson {

    /** Telegram message id for the message containing last depicted menu in the chat **/
    private int chatMenuMessageId;

    public Adopter() {
    }

    public Adopter(long chatId) {
        super(chatId, null);
    }

    public Adopter(long chatId, String firstName) {
        super(chatId, firstName);
    }

    @Override
    public String toString() {
        return "Adopter{" +
                super.toString() +
                ", chatMenuMessageId=" + chatMenuMessageId +
                "}";
    }

    public int getChatMenuMessageId() {
        return chatMenuMessageId;
    }

    public Adopter setChatMenuMessageId(int chatMenuMessageId) {
        this.chatMenuMessageId = chatMenuMessageId;
        return this;
    }
}


