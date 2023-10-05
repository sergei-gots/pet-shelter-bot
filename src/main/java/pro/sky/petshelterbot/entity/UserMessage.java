package pro.sky.petshelterbot.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "user_messages")
public class UserMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;
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

    public String getName() {
        return name;
    }

    public void setName(String key) {
        this.name = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String message) {
        this.content = message;
    }

    public UserMessage() {}
}
