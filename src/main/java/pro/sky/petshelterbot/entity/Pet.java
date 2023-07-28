package pro.sky.petshelterbot.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String species;

    private String name;

    @JoinColumn(name = "shelter_id")
    @ManyToOne
    private Shelter shelter;

    @JoinColumn(name = "adopter_id")
    @ManyToOne
    private Adopter adopter;

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

    private String imgPath;


    public Pet() {
    }

    public Pet(String species, String name, Shelter shelter) {
        this(species, name, shelter, false);
    }

    public Pet(String species, String name, Shelter shelter, boolean disabled) {
        this.species = species;
        this.name = name;
        this.shelter = shelter;
        this.disabled = disabled;
    }

    public Pet(Long id, String species, String name, Shelter shelter, boolean disabled) {
        this(species, name, shelter, disabled);
        this.id = id;
    }


    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public Long getId() {
        return id;
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

    public Adopter getAdopter() {
        return adopter;
    }

    public void setAdopter(Adopter adopter) {
        this.adopter = adopter;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", species='" + species + '\'' +
                ", name='" + name + '\'' +
                ", shelter=" + shelter +
                ", adopter=" + adopter +
                ", disabled=" + disabled +
                ", adoptionDate=" + adoptionDate +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return disabled == pet.disabled && Objects.equals(id, pet.id) && Objects.equals(species, pet.species) && Objects.equals(name, pet.name) && Objects.equals(shelter, pet.shelter) && Objects.equals(adopter, pet.adopter) && Objects.equals(adoptionDate, pet.adoptionDate) && Objects.equals(imgPath, pet.imgPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, species, name, shelter, adopter, disabled, adoptionDate, imgPath);
    }
}
