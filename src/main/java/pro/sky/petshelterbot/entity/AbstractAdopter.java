package pro.sky.petshelterbot.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractAdopter extends AbstractPerson {
    public AbstractAdopter() {
    }

    public AbstractAdopter(long chatId, String firstName) {
        super(chatId, firstName);
    }

    @Override
    public String toString() {
        return "Adopter{" +
                super.toString() +
                "}";
    }
}
