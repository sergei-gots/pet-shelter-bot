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
    public void setShelter(Shelter shelter) {
        super.setShelter(shelter);
        setChatState((shelter!=null)?
                ChatState.MENU_NAVIGATION :
                ChatState.INITIAL_STATE
        );

    }

    @Override
    public String toString() {
        return "Adopter{" +
                super.toString() +
                "}";
    }

}


