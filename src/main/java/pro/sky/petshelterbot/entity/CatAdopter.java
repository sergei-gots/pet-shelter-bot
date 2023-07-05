package pro.sky.petshelterbot.entity;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cat_adopters")
public class CatAdopter extends AbstractAdopter {

    public CatAdopter() {
    }

    public CatAdopter(long chatId, String firstName) {
        super(chatId, firstName);
    }
}


