package pro.sky.petshelterbot.entity;

import javax.persistence.*;

@Entity
@Table(name="cats")
public class Cat extends Pet {



    public Cat() {
    }

    public Cat(String name) {
        super(-1, name, false);
    }
    public Cat(int id, String name, boolean disabled) {
        super(id, name, disabled);
    }

    @Override
    public String toString() {
        return "Cat{" + super.toString() + '}';
    }
}
