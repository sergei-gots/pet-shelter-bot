package pro.sky.petshelterbot.entity;

import javax.persistence.*;

@Entity
@Table(name = "buttons")
public class Button {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;
    private String text;
    private String shelter_id;
    private String chapter;

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getShelter_id() {
        return shelter_id;
    }

    public void setShelter_id(String shelter_id) {
        this.shelter_id = shelter_id;
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
