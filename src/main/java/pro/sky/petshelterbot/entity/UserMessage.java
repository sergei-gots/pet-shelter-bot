package pro.sky.petshelterbot.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "user_messages")
public class UserMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String message;
    @JoinColumn(name="shelter_id")
    @ManyToOne
    private Shelter shelter;

    public Long getId() {
        return id;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserMessage() {}
}
