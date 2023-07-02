package pro.sky.petshelterbot.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Adopter extends Person {
    public Adopter() {
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
