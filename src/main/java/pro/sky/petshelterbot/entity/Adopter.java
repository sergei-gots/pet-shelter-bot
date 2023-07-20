package pro.sky.petshelterbot.entity;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "adopters")
public class Adopter extends AbstractPerson {




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
                "}";
    }

}


