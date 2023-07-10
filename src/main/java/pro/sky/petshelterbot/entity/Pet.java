package pro.sky.petshelterbot.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String species;

    private String name;

    @JoinColumn(name="shelter_id")
    @ManyToOne
    private Shelter shelter;
    /**
     * true if the pet is a pet with disabilities.
     * Default value is true
     */
    private boolean disabled;

    /**
     * Date of adoption could have the next values:
     * - null when the pet is in shelter;
     * - in the future when the pet either on trial or supposed to be on trial;
     * - in the past when the pet is successfully adopted
     **/
    @Column(name = "adoption_date")
    private LocalDate adoptionDate;


    public Pet() {
    }

    public Pet(String species, String name, Shelter shelter) {
        this(-1, species, name, shelter, false);
    }

    public Pet(long id, String species, String name, Shelter shelter, boolean disabled) {
        this.id = id;
        this.species = species;
        this.name = name;
        this.shelter = shelter;
        this.disabled = disabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
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
        return "Pet{" +
                "id=" + id +
                ", the " +  species +
                ", name='" + name + '\'' +
                ", disabled=" + disabled +
                ((disabled) ?
                        ", with some disabilities" :
                        ", without any disabilities"
                ) +
                '}';
    }
}
