package pro.sky.petshelterbot.entity;


import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "volunteers")
public class Volunteer extends AbstractPerson {

    @JoinColumn(name="shelter_id")
    @ManyToOne
    private Shelter shelter;

    private boolean available = true;

    public Volunteer() {
    }

    public Volunteer(long chatId, String firstName, Shelter shelter) {
        super(chatId, firstName);
        this.shelter = shelter;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    @Override
    public String toString() {
        return "Volunteer{" +
                super.toString() +
                shelter +
                "}";
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}


