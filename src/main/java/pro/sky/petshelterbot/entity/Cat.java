package pro.sky.petshelterbot.entity;

import javax.persistence.*;

@Entity
@Table(name= "cats")
public class Cat extends Pet {

    @JoinColumn(name = "cat_adopter_id")
    @ManyToOne
    private CatAdopter catAdopter;

    public Cat() {
    }

    public Cat(String name) {
        super(-1, name, false);
    }
    public Cat(int id, String name, boolean disabled) {
        super(id, name, disabled);
    }

    public Cat(int id, String name, boolean disabled, CatAdopter catAdopter) {
        super(id, name, disabled);
        this.catAdopter = catAdopter;
    }

    public CatAdopter getAdopter() {
        return catAdopter;
    }

    public void setCatAdopter(CatAdopter CatAdopter) {
        this.catAdopter = CatAdopter;
    }

    @Override
    public String toString() {
        return "Cat{" + super.toString() + '}';
    }
}
