package pro.sky.petshelterbot.entity;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "volunteers")
public class Volunteer extends AbstractPerson {

    private boolean available = true;

    public Volunteer() {
    }

    public Volunteer(long chatId, String firstName, Shelter shelter) {
        super(chatId, firstName, shelter);
    }


    @Override
    public String toString() {
        return "Volunteer{" +
                super.toString() +
                "}";
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}


