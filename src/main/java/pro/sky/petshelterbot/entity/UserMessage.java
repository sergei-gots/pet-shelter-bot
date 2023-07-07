package pro.sky.petshelterbot.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "user_messages")
public class UserMessage {
    public enum Key {
        CAT_SHELTER_INFO_ABOUT,
        CAT_SHELTER_INFO_KITTY_AT_HOME,
        /** This enum-value is to demonstrate an example
         * how to describe short messages immediately in xml-migration-file
         * @see 'db/changelog/insert/insert-into-user-messages.xml'
         */
        SHORT_MESSAGE_TEXT_EXAMPLE
    }

    @Id
    @Enumerated(EnumType.STRING)
    private Key key;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String text;

    public UserMessage() {
    }

    public UserMessage(Key key, String text) {
        this.key = key;
        this.text = text;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
