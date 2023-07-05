package pro.sky.petshelterbot.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "shelters")
public class Shelter {

    public enum SHELTER_ID {
        CAT_SHELTER, DOG_SHELTER
    }

    @Id
    private SHELTER_ID id;

    private String name;

    public Shelter() {
    }

    public Shelter(SHELTER_ID shelterId, String name) {
        this.id = shelterId;
        this.name = name;
    }

    public SHELTER_ID getId() {
        return id;
    }

    public void setId(SHELTER_ID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Shelter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
