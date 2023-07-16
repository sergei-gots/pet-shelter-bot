package pro.sky.petshelterbot.entity;

import pro.sky.petshelterbot.constants.ButtonKeys;

import javax.persistence.*;

@Entity
@Table(name = "buttons")
public class Button implements ButtonKeys {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;
    private String text;
    private Long shelterId;
    private String chapter;
    private Integer position;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public Long getShelterId() {
        return shelterId;
    }

    public void setShelterId(Long shelter_id) {
        this.shelterId = shelter_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }
}
