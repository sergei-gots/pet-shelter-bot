package pro.sky.petshelterbot.entity;

import javax.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
abstract class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    /**
     * true if the pet is a pet with disabilities.
     * Default value is true
     */
    private boolean disabled;

    /** Date of adoption could have the next values:
     * - null when the pet is in shelter;
     * - in the future when the pet either on trial or supposed to be on trial;
     * - in the past when the pet is successfully adopted
     **/
    private LocalDate adoptionDate;




    public Pet() {
    }

    public Pet(String name) {
        this(-1, name, false);
    }

    public Pet(long id, String name, boolean disabled) {
        this.id = id;
        this.name = name;
        this.disabled = disabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns true if the pet is a pet with disabilities
     **/
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets property indicating that the pet is a pet with disabilities
     **/
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public LocalDate getAdoptionDate() {
        return adoptionDate;
    }

    public void setAdoptionDate(LocalDate adoptionDate) {
        this.adoptionDate = adoptionDate;
    }



    @Override
    public String toString() {
        return  "id=" + id +
                ", name='" + name + '\'' +
                ((disabled)?
                    ", with some disabilities":
                    ", without any disabilities"
                );
    }
}
